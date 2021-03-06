/*
 * org.openmicroscopy.shoola.util.roi.ROICreationException 
 *
  *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2007 University of Dundee. All rights reserved.
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
package org.openmicroscopy.shoola.util.roi.exception;

//Java imports

//Third-party libraries

//Application-internal dependencies

/** 
 * Reports an error occurred while creating a ROI.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since OME3.0
 */
public class ROICreationException
	extends Exception
{
	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message	Short explanation of the problem.
	 * @param cause		The exception that caused this one to be risen.
	 */
	public ROICreationException(String message, Throwable cause) 
	{
		super(message, cause);
	}
	
	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message	Short explanation of the problem.
	 */
	public ROICreationException(String message) { super(message); }

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param e	The exception wrapped by the ROICreationException.
	 */
	public ROICreationException(Exception e)
	{
		super(e);
	}
	
	/** Constructs a default exception. */
	public ROICreationException()
	{
		super();
	}
	
}


