package ome.tools.hibernate;

import java.util.Map;
import java.util.Set;

import ome.model.IObject;
import ome.model.meta.Event;
import ome.model.meta.EventDiff;
import ome.model.meta.EventLog;

import org.hibernate.Session;

public class EventCreator {

    public void doIt(Session s, EventInterceptor.Events events){
    		// perform inserting audit logs for entities those were enlisted in
			// inserts,
            // updates, and deletes sets...

    		// Get CurrentModule here
    		Event e = new Event();
    //		e.setName("test");
    		s.save(e);
    		
    		EventLog l = new EventLog();
    		l.setEvent(e);
    		l.getDetails().setOwner(null); // TODO null? and current user
    		s.save(l);
    		
    		makeDiffs(s,l,events.inserts,"INSERT");
    		makeDiffs(s,l,events.updates,"UPDATES");
    		makeDiffs(s,l,events.deletes,"DELETES");
    }
	
    private void makeDiffs(Session s, EventLog l, Map<Class,Set<IObject>> m, String action){
		for (Class key : m.keySet()){
			Set<IObject> ids = m.get(key);
			if (ids.size()>0) {
				EventDiff d = new EventDiff();
				// TODO Delete. Broken. d.setAction(action);
				d.setLogs(l);
				int[] ints = new int[ids.size()];
				IObject omes[] = (IObject[]) ids.toArray(new IObject[ids.size()]);
				for (int i = 0; i < ints.length; i++) {
					ints[i] = omes[i].getId().intValue(); 
				}
//				d.setIdList("CONVERTED TO STRING"); // FIXME (ints); 
//				d.setType(key.getName());
//				s.save(d); TODO Delete. Broken.
			}
		}
    }
}
