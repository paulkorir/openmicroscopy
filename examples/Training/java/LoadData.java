/*
 * training.LoadData 
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006-2011 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */
package training;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import omero.client;
import omero.api.IContainerPrx;
import omero.api.IQueryPrx;
import omero.api.ServiceFactoryPrx;
import omero.model.Dataset;
import omero.model.IObject;
import omero.model.Image;
import omero.model.Project;
import omero.model.Screen;
import omero.model.Well;
import omero.sys.ParametersI;
import pojos.DatasetData;
import pojos.ImageData;
import pojos.PixelsData;
import pojos.PlateData;
import pojos.ProjectData;
import pojos.ScreenData;
import pojos.WellData;
//Java imports
//Third-party libraries
//Application-internal dependencies
/** 
 * 
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
 
public class LoadData
{

	/** The server address.*/
	private String hostName = "localhost";
	
	/** The port to use.*/
	private int port = 4064; //default port
	
	/** The username.*/
	private String userName = "root";
	
	/** The password.*/
	private String password = "omero";
	
	/** Reference to the client.*/
	private client client;
	
	/** The service factory.*/
	private ServiceFactoryPrx entryUnencrypted;
	
	/** The id of a dataset.*/
	private long datasetId = 51;
	
	/** The id of an image.*/
	private long imageId = 456;
	
	/** The id of a plate.*/
	private long plateId = 53;
	
	/** The id of the plate acquisition corresponding to the plate.*/
	private long plateAcquisitionId = 0;
	
	/** First connect.*/
	private void connect()
		throws Exception
	{
		
		client = new client(hostName, port);
		ServiceFactoryPrx entry = client.createSession(userName, password);
		// if you want to have the data transfer encrypted then you can 
		// use the entry variable otherwise use the following 
		client unsecureClient = client.createClient(false);
		entryUnencrypted = unsecureClient.getSession();
		
		long userId = entryUnencrypted.getAdminService().getEventContext().userId;
		
		long groupId = entryUnencrypted.getAdminService().getEventContext().groupId;
	}
	
	/** 
	 * Retrieve the projects owned by the user currently logged in.
	 * 
	 * If a project contains datasets, the datasets will automatically be loaded.
	 */
	private void loadProjects()
		throws Exception
	{
		IContainerPrx proxy = entryUnencrypted.getContainerService();
		ParametersI param = new ParametersI();
		long userId = entryUnencrypted.getAdminService().getEventContext().userId;
		param.exp(omero.rtypes.rlong(userId));
		param.leaves(); //indicate to load the images
		//param.noLeaves(); //no images loaded, this is the default value.
		List<IObject> results = proxy.loadContainerHierarchy(
				Project.class.getName(), new ArrayList(), param);
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		Iterator<IObject> i = results.iterator();
		ProjectData project;
		Set<DatasetData> datasets;
		Iterator<DatasetData> j;
		DatasetData dataset;
		while (i.hasNext()) {
			project = new ProjectData((Project) i.next());
			datasets = project.getDatasets();
			j = datasets.iterator();
			while (j.hasNext()) {
				dataset = j.next();
				//Do something here
				//If images loaded.
				//dataset.getImages();
			}
		}
	}
	
	/** 
	 * Retrieve the datasets owned by the user currently logged in.
	 */
	private void loadDatasets()
		throws Exception
	{
		IContainerPrx proxy = entryUnencrypted.getContainerService();
		ParametersI param = new ParametersI();
		long userId = entryUnencrypted.getAdminService().getEventContext().userId;
		param.exp(omero.rtypes.rlong(userId));
		param.leaves(); //indicate to load the images
		//param.noLeaves(); //no images loaded, this is the default value.
		List<IObject> results = proxy.loadContainerHierarchy(
				Dataset.class.getName(), new ArrayList(), param);
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		Iterator<IObject> i = results.iterator();
		DatasetData dataset;
		Set<ImageData> images;
		Iterator<ImageData> j;
		ImageData image;
		while (i.hasNext()) {
			dataset = new DatasetData((Dataset) i.next());
			images = dataset.getImages();
			j = images.iterator();
			while (j.hasNext()) {
				image = j.next();
				//Do something
			}
		}
	}
	
	/** 
	 * Retrieve the images contained in a dataset.
	 * 
	 * In that case, we specify the dataset's id.
	 */
	private void loadImagesInDataset()
		throws Exception
	{
		IContainerPrx proxy = entryUnencrypted.getContainerService();
		ParametersI param = new ParametersI();
		List<Long> ids = new ArrayList<Long>();
		ids.add(datasetId);
		param.leaves(); //indicate to load the images
		List<IObject> results = proxy.loadContainerHierarchy(
				Dataset.class.getName(), ids, param);
		
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		DatasetData dataset = new DatasetData((Dataset) results.get(0));
		Set<ImageData> images = dataset.getImages();
		Iterator<ImageData> j = images.iterator();
		ImageData image;
		while (j.hasNext()) {
			image = j.next();
			//Do something
		}
	}
	
	/** 
	 * Retrieve an image if the identifier is known.
	 */
	private void loadImage()
		throws Exception
	{
		IContainerPrx proxy = entryUnencrypted.getContainerService();
		List<Long> ids = new ArrayList<Long>();
		ids.add(imageId);
		List<Image> results = proxy.getImages(Image.class.getName(), ids, 
				new ParametersI());
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		ImageData image = new ImageData(results.get(0));
		PixelsData pixels = image.getDefaultPixels();
		int sizeZ = pixels.getSizeZ(); // The number of z-sections.
		int sizeT = pixels.getSizeT(); // The number of timepoints.
		int sizeC = pixels.getSizeC(); // The number of channels.
		int sizeX = pixels.getSizeX(); // The number of pixels along the X-axis.
		int sizeY = pixels.getSizeY(); // The number of pixels along the Y-axis.
	}
	
	/** 
	 * Retrieve Screening data owned by the user currently logged in.
	 * 
	 * To learn about the model go to ScreenPlateWell.
	 * Note that the wells are not loaded.
	 */
	private void loadScreens()
		throws Exception
	{
		IContainerPrx proxy = entryUnencrypted.getContainerService();
		ParametersI param = new ParametersI();
		long userId = entryUnencrypted.getAdminService().getEventContext().userId;
		param.exp(omero.rtypes.rlong(userId));
		
		List<IObject> results = proxy.loadContainerHierarchy(
				Screen.class.getName(), new ArrayList(), param);
		//You can directly interact with the IObject or the Pojos object.
		//Follow interaction with the Pojos.
		Iterator<IObject> i = results.iterator();
		ScreenData screen;
		Set<PlateData> plates;
		Iterator<PlateData> j;
		PlateData plate;
		while (i.hasNext()) {
			screen = new ScreenData((Screen) i.next());
			plates = screen.getPlates();
			j = plates.iterator();
			while (j.hasNext()) {
				plate = j.next();
			}
		}
	}
	
	/** 
	 * Retrieve Screening data owned by the user currently logged in.
	 * 
	 * To learn about the model go to ScreenPlateWell.
	 * Note that the wells are not loaded.
	 */
	private void loadWells()
		throws Exception
	{
		IQueryPrx proxy = entryUnencrypted.getQueryService();
		StringBuilder sb = new StringBuilder();
		ParametersI param = new ParametersI();
		param.addLong("plateID", plateId);
		sb.append("select well from Well as well ");
		sb.append("left outer join fetch well.plate as pt ");
		sb.append("left outer join fetch well.wellSamples as ws ");
		sb.append("left outer join fetch ws.plateAcquisition as pa ");
		sb.append("left outer join fetch ws.image as img ");
		sb.append("left outer join fetch img.pixels as pix ");
        sb.append("left outer join fetch pix.pixelsType as pt ");
        sb.append("where well.plate.id = :plateID");
        if (plateAcquisitionId > 0) {
        	 sb.append(" and pa.id = :acquisitionID");
        	 param.addLong("acquisitionID", plateAcquisitionId);
        }
        List<IObject> results = proxy.findAllByQuery(sb.toString(), param);
        Iterator<IObject> i = results.iterator();
        WellData well;
        while (i.hasNext()) {
			well = new WellData((Well) i.next());
			//Do something
		}
	}
	
	/**
	 * Shows how to connect to omero.
	 */
	LoadData()
	{
		try {
			connect(); //First connect.
			loadProjects();
			loadDatasets();
			loadImagesInDataset();
			loadImage();
			loadScreens();
			loadWells();
			client.closeSession();
		} catch (Exception e) {
			if (client != null) client.closeSession();
		}
		
		
	}
	
	public static void main(String[] args) 
	{
		new LoadData();
	}
	
}
