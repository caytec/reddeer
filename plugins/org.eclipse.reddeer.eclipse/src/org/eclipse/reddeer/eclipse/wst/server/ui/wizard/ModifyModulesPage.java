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
package org.eclipse.reddeer.eclipse.wst.server.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Wizard page for adding and removing modules on the server.
 * 
 * @author Lucia Jelinkova
 * @author Radoslav Rabara
 */
public class ModifyModulesPage extends WizardPage {
	
	public ModifyModulesPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Adds modules with the specified names.
	 *
	 * @param projectNames names of the modules to be added
	 */
	public void add(String... projectNames) {
		for (String project : projectNames) {
			new DefaultTreeItem(new DefaultTree(referencedComposite), project).select();
			new PushButton(referencedComposite, "Add >").click();
		}
	}

	/**
	 * Adds all available modules to the server.
	 *
	 */
	public void addAll() {
		new PushButton(referencedComposite, "Add All >>").click();
	}

	/**
	 * Removes modules with the specified names.
	 *
	 * @param projectNames names of the modules to be removed from the server
	 */
	public void remove(String... projectNames) {
		for (String project : projectNames) {
			new DefaultTreeItem(new DefaultTree(referencedComposite, 1), project).select();
			new PushButton(referencedComposite, "< Remove").click();
		}
	}

	/**
	 * Removes all modules from the server.
	 */
	public void removeAll() {
		new PushButton(referencedComposite, "<< Remove All").click();
	}

	/**
	 * Returns available modules that are not configured on the server or are
	 * not proposed to be configured on the server.
	 *
	 * @return list of modules that are not configured on the server or are not
	 *         proposed to be configured on the server
	 */
	public List<String> getAvailableModules() {
		return getItemsFromTable(0);
	}

	/**
	 * Returns modules that are configured or are proposed to be configured on
	 * the server.
	 *
	 * @return list of modules that are configured or are proposed to be
	 *         configured on the server
	 */
	public List<String> getConfiguredModules() {
		return getItemsFromTable(1);
	}

	private List<String> getItemsFromTable(int tableIndex) {
		List<String> items = new ArrayList<String>();
		for (TreeItem ti : new DefaultTree(referencedComposite, tableIndex).getAllItems()) {
			items.add(ti.getText());
		}
		return items;
	}
}
