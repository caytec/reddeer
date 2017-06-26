/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.reddeer.eclipse.ui.wizards.datatransfer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.common.condition.AbstractWaitCondition;
import org.eclipse.reddeer.common.logging.Logger;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.exception.EclipseLayerException;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.Tree;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;

/**
 * Wizard page for importing external projects into the workspace.
 * 
 * @author Lucia Jelinkova
 *
 */
public class WizardProjectsImportPage extends WizardPage {
	
	private static final Logger log = Logger.getLogger(WizardProjectsImportPage.class);

	/**
	 * Instantiates a new wizard projects import page.
	 */
	public WizardProjectsImportPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}
	
	/**
	 * Represents an imported project.
	 */
	public static class ImportProject {
		
		public boolean isChecked;
		
		public String name;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ImportProject[" + isChecked + ", " + name + "]";
		}
	}

	/**
	 * Sets root directory.
	 * 
	 * @param directory Root directory
	 */
	public void setRootDirectory(String directory){
		log.info("Setting root directory to '" + directory + "'");
		setPath("Select root directory:", directory);
	}
	
	/**
	 * Sets archive file.
	 * 
	 * @param file File
	 */
	public void setArchiveFile(String file){
		log.info("Settig archive file to '" + file + "'");
		setPath("Select archive file:", file);
	}

	/**
	 * Sets whether to copy projects into workspace.
	 * 
	 * @param copy Indicates whether to copy projects into workspace
	 */
	public void copyProjectsIntoWorkspace(boolean copy){
		log.info("Setting copy checkbox to " + copy);
		if (isFileSystem()){
			new CheckBox(referencedComposite, "Copy projects into workspace").toggle(copy);
		} else {
			throw new EclipseLayerException("You cannot set Copy projects into workspace checkbox when you're importing from ZIP file");
		}
	}
	
	/**
	 * Returns list of projects.
	 * 
	 * @return List of projects
	 */
	public List<ImportProject> getProjects(){
		List<ImportProject> projects = new ArrayList<ImportProject>();
		
		Tree projectsTree = getProjectsTree();
		for (TreeItem item : projectsTree.getItems()){
			ImportProject project = new ImportProject();
			project.isChecked = item.isChecked();
			project.name = getProjectLabel(item.getText());
			projects.add(project);
		}
		
		return projects;
	}
	
	/**
	 * Selects a given projects.
	 * 
	 * @param projects Projects
	 */
	public void selectProjects(String... projects){
		log.info("Selecting projects");
		deselectAllProjects();
		Tree projectsTree = getProjectsTree();
		
		for (String projectName : projects){
			TreeItem  projectItem = getProjectTreeItem(projectsTree, projectName);
			projectItem.setChecked(true);
		}
	}
	
	/**
	 * Selects all projects.
	 */
	public void selectAllProjects(){
		log.info("Selecting all projects");
		new PushButton(referencedComposite, "Select All").click();
	}
	
	/**
	 * Deselects all projects.
	 */
	public void deselectAllProjects(){
		log.info("Deselecting all projects");
		new PushButton(referencedComposite, "Deselect All").click();
	}
	
	/**
	 * Sets the path.
	 *
	 * @param radioText the radio text
	 * @param path the path
	 */
	protected void setPath(String radioText, String path){
		new RadioButton(referencedComposite, radioText).click();
		if(radioText.equals("Select root directory:")){
			new DefaultCombo(referencedComposite, 0).setText(path);
		} else {
			new DefaultCombo(referencedComposite, 1).setText(path);
		}
		new PushButton(referencedComposite, "Refresh").click();
		new WaitUntil(new ProjectIsLoaded(getProjectsTree()), TimePeriod.DEFAULT);
	}
	
	private boolean isFileSystem() {
		return new RadioButton(referencedComposite, "Select root directory:").isSelected();
	}
	
	private Tree getProjectsTree() {
		return new DefaultTree(referencedComposite);
	}
	
	private TreeItem getProjectTreeItem(Tree projectsTree, String projectName) {
		for (TreeItem item : projectsTree.getItems()){
			if (projectName.equals(getProjectLabel(item.getText()))){
				return item;
			}
		}
		throw new IllegalStateException("Project " + projectName + " is not available");
	}
	
	private String getProjectLabel(String project){
		return project.substring(0, project.indexOf('(')).trim();
	}
	
	private class ProjectIsLoaded extends AbstractWaitCondition {

		private Tree tree;
		
		private ProjectIsLoaded(Tree tree) {
			this.tree = tree;
		}
		
		@Override
		public boolean test() {
			return !tree.getItems().isEmpty();
		}

		@Override
		public String description() {
			return "at least one project is loaded";
		}
	}
}
