package org.restnucleus.test;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.restnucleus.dao.GenericRepository;
import org.restnucleus.dao.Model;

public class DbHelper {

	final private GenericRepository dao;

	public DbHelper(GenericRepository genericRepository) {
		dao = genericRepository;
	}

	public GenericRepository getDao() {
		return dao;
	}

	public <E extends Model> void persist(
			Map<Class<? extends Model>, List<? extends Model>> data) {
		if (null != data) {
			for (Entry<Class<? extends Model>, List<? extends Model>> e : data
					.entrySet()) {
				try {
					if (null == e.getValue() || e.getValue().size() < 1) {
						System.out.println("no data found for: "
								+ e.getKey().getSimpleName());
					} else {
						for (Model m : e.getValue()) {
							if (m.getId() == null)
								dao.add(m);
						}
						System.out.println(e.getKey().getSimpleName()
								+ " populated with " + e.getValue().size()
								+ " entities");
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

}
