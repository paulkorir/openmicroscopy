/*
 * org.openmicroscopy.shoola.agents.treeviewer.view.DeleteBox
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2008 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package org.openmicroscopy.shoola.agents.treeviewer.view;


//Java imports
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.util.ui.MessageBox;
import pojos.DatasetData;
import pojos.FileAnnotationData;
import pojos.ImageData;
import pojos.ProjectData;
import pojos.TagAnnotationData;

/** 
 * A modal dialog asking what the user wants to delete.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class DeleteBox 
	extends MessageBox
{

	/** The title of the dialog. */
	private static final String		TITLE = "Confirm delete";
	
	/** The default delete text. */
	private static final String		DEFAULT_TEXT = "Are you sure you want to " +
			"delete the selected ";

	/** Delete the objects and the contents. */
	private JRadioButton 			withContent;
	
	/** Delete the objects but not the contents. */
	private JRadioButton 			withoutContent;
	
	/** Delete the related annotations if selected. */
	private JCheckBox				withAnnotation;
	
	/** The type of object to remove. */
	private Class					type;
	
	/** The Name space of the object to remove. */
	private String					nameSpace;
	
	/** Flag indicating if the objects have been annotated. */
	private boolean					annotation;
	
	/** Flag indicating if the objects have children. */
	private boolean					children;
    
	/** The components corresponding to the annotation. */
	private Map<JCheckBox, Class>	annotationTypes;
	
	/** The UI component hosting the various annotations types. */
	private JPanel					typesPane;
	
	/**
	 * Creates and formats a check box.
	 * 
	 * @param name The name to display.
	 * @return See above.
	 */
	private JCheckBox createBox(String name)
	{
		JCheckBox box = new JCheckBox(name);
		Font f = box.getFont();
		int size = f.getSize()-2;
		Font newFont = f.deriveFont(Font.ITALIC, size);
		box.setSelected(true);
		box.setFont(newFont);
		box.setEnabled(false);
		return box;
	}
	
	/** Initializes the components composing the display. */
	private void initComponents()
	{
		withAnnotation = new JCheckBox("Also delete the annotations.");
		withContent = new JRadioButton("Also delete contents.");
		withoutContent = new JRadioButton("Do not delete contents.");
		ButtonGroup group = new ButtonGroup();
		group.add(withContent);
		group.add(withoutContent);
		withoutContent.setSelected(true);
		annotationTypes = new LinkedHashMap<JCheckBox, Class>();
		//annotationTypes.put(createBox("Rating"), RatingAnnotationData.class);
		annotationTypes.put(createBox("Tag"), TagAnnotationData.class);
		//annotationTypes.put(createBox("Comment"), TextualAnnotationData.class);
		annotationTypes.put(createBox("Attachment"), FileAnnotationData.class);
		
		withAnnotation.addChangeListener(new ChangeListener() {
		
			public void stateChanged(ChangeEvent e) {
				layoutAnnotationTypes();
			}
		
		});
		typesPane = new JPanel();
		typesPane.setLayout(new GridBagLayout());
	}
	
	/** Lays out the annotation types. */
	private void layoutAnnotationTypes()
	{
		boolean b = withAnnotation.isSelected();
		typesPane.setEnabled(b);
		Iterator<JCheckBox> i = annotationTypes.keySet().iterator();
		while (i.hasNext()) {
			(i.next()).setEnabled(b);
		}
	}
	
	/** Builds and lays out the component. */
	private void layoutComponents()
	{
		Iterator<JCheckBox> i = annotationTypes.keySet().iterator();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 2, 2, 0);
		while (i.hasNext()) {
            c.gridx = 0;
            ++c.gridy;
       	 	c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 1.0;  
            typesPane.add(Box.createHorizontalStrut(30), c);
            c.gridx++;
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            //c.fill = GridBagConstraints.HORIZONTAL;
            //c.weightx = 1.0;
            typesPane.add(i.next(), c);  
        }
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		boolean add = false;
		if (ImageData.class.equals(type)) {
			add = true;
			if (annotation) {
				p.add(withAnnotation);
				p.add(typesPane);
			}
		} else if (DatasetData.class.equals(type) || 
				ProjectData.class.equals(type)) {
			add = true;
			if (children) {
				p.add(withContent);
				p.add(withoutContent);
			}
			if (annotation) {
				p.add(withAnnotation);
				p.add(typesPane);
			}
		} else if (TagAnnotationData.class.equals(type)) {
			if (TagAnnotationData.INSIGHT_TAGSET_NS.equals(nameSpace)) {
				add = true;
				if (children) {
					p.add(withContent);
					p.add(withoutContent);
				}
			}
		}
		if (add)
			addBodyComponent(p);
	}
	
	/**
	 * Returns the message corresponding to the specified class and
	 * the number of selected items.
	 * 
	 * @param type			The type of object to handle.
	 * @param number		The number of object to remove.
	 * @param nameSpace		Name space related to the data object if any.
	 * @param annotation	Pass <code>true</code> if the objects have been 
	 * 						annotated, <code>false</code> otherwise.
	 * @param children		Pass <code>true</code> if the objects have been 
	 * 						annotated, <code>false</code> otherwise.
	 * @return See above. 
	 */
	private static String getMessage(Class type, int number, String nameSpace,
						boolean annotation, boolean children)
	{
		StringBuffer buffer = new StringBuffer(); 
		String end = "?";
		if (number > 1) end = "s?";
		if (ImageData.class.equals(type)) {
			buffer.append(DEFAULT_TEXT+" Image"+end);
			buffer.append("\n");
			if (annotation || children) buffer.append("If yes, ");
		} else if (DatasetData.class.equals(type)) {
			buffer.append(DEFAULT_TEXT+" Dataset"+end);
			buffer.append("\n");
			if (annotation || children) buffer.append("If yes, ");
		} else if (ProjectData.class.equals(type)) {
			buffer.append(DEFAULT_TEXT+" Project"+end);
			buffer.append("\n");
			if (annotation || children) buffer.append("If yes, ");
		} else if (FileAnnotationData.class.equals(type)) {
			buffer.append(DEFAULT_TEXT+" File"+end);
			buffer.append("\n");
		} else if (TagAnnotationData.class.equals(type)) {
			if (TagAnnotationData.INSIGHT_TAGSET_NS.equals(nameSpace)) {
				buffer.append(DEFAULT_TEXT+" Tag Set"+end);
				buffer.append("\n");
				if (children) buffer.append("If yes, ");
			} else {
				buffer.append(DEFAULT_TEXT+" Tag"+end);
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
	
	/**
	 * Creates a new instance.
	 * 
	 * @param type			The type of objects to delete.
	 * @param annotation	Pass <code>true</code> if the object has 
	 * 						been annotated, <code>false</code> otherwise.
	 * @param children		Pass <code>true</code> if the object has 
	 * 						children, <code>false</code> otherwise.
	 * @param number		The number of objects to delete.
	 * @param nameSpace		Name space related to the data object if any.
	 * @param parent 		The parent of the frame
	 */
	public DeleteBox(Class type, boolean annotation, boolean children,
			int number, String nameSpace, JFrame parent)
	{
		super(parent, TITLE, 
				DeleteBox.getMessage(type, number, nameSpace,
						annotation, children));
		this.nameSpace = nameSpace;
		this.type = type;
		this.annotation = annotation;
		this.children = children;
		initComponents();
		layoutComponents();
		pack();
	}
    
    /**
     * Returns <code>true</code> if the objects contained in the objects
     * to delete have to be deleted.
     * 
     * @return See above.
     */
    public boolean deleteContents()
    {
    	if (ImageData.class.equals(type)) return false;
    	return (withContent.isSelected());
    }
    
    /**
     * Returns <code>true</code> if the annotations related to the objects
     * to delete have to be deleted.
     * 
     * @return See above.
     */
    public boolean deleteAnnotations() { return withAnnotation.isSelected(); }
    
    /**
     * Returns the types of annotations to delete.
     * 
     * @return See above.
     */
    public List<Class> getAnnotationTypes()
    {
    	List<Class> types = new ArrayList<Class>();
    	Iterator<JCheckBox> i = annotationTypes.keySet().iterator();
    	JCheckBox box;
    	while (i.hasNext()) {
    		box = i.next();
			if (box.isSelected()) 
				types.add(annotationTypes.get(box));
		}
    	//if (types.size() == annotationTypes.size()) return null;
    	return types;
    }
    
}
