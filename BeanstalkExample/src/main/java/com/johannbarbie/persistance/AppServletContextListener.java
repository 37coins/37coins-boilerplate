package com.johannbarbie.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.johannbarbie.persistance.dao.GenericRepository;
import com.johannbarbie.persistance.dao.Model;
import com.johannbarbie.persistance.exceptions.EntityNotFoundException;
import com.johannbarbie.persistance.exceptions.ParameterMissingException;
import com.johannbarbie.persistance.exceptions.PersistanceException;

public class AppServletContextListener implements ServletContextListener {

	public GenericRepository gr = new GenericRepository();
	Map<String, Model> mapping = new HashMap<String, Model>();

	public void contextDestroyed(ServletContextEvent arg0) {
		PersistenceConfiguration.getInstance().closeEntityManagerFactory();
		System.out.println("ServletContextListener destroyed");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		ServletContext sc = arg0.getServletContext();
		@SuppressWarnings("unchecked")
		Set<String> ss = (Set<String>) sc
				.getResourcePaths("/WEB-INF/test_data/");
		List<String> s = new ArrayList<String>(ss);
		Collections.sort(s);
		for (String f : s) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				List<Model> values = null;
				InputStream ip = sc.getResourceAsStream(f);
				TypeReference<?> tp = null;
//				if (f.contains("user")) {
//					tp = new TypeReference<List<UserDTO>>() {};
//				}
//				if (f.contains("userrelation")) {
//					tp = new TypeReference<List<UserRelationDTO>>() {};
//				}
				if (null!=tp){
					values = mapper.readValue(ip, tp);
					persist(values, tp.getType());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			gr.stopAttach();
		} catch (PersistanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ServletContextListener started");
	}

	public void persist(List<Model> l, Type type) throws PersistanceException,
			EntityNotFoundException, ParameterMissingException, JsonGenerationException, JsonMappingException, IOException {
		if (null == l || l.size() < 1) {
			System.out.println("no data found for: " + type.toString());
			return;
		}
		// no need to initialize
		if (gr.findAll(l.get(0).getClass()).size() > 0) {
			System.out.println("skipped initializing " + type.toString()
					+ ", data exists");
			return;
		}
		int i = 0;
		for (Model m : l) {
//			if (m instanceof UserDTO && ((UserDTO)m).getCurrentCharacter()!=null){
//				Type t = new TypeReference<List<CharacterDTO>>() {}.getType();
//				UserDTO u = (UserDTO)m;
//				CharacterDTO cc = (CharacterDTO)mapping.get(t.toString() + u.getCurrentCharacter().getId());
//				u.setCurrentCharacter(cc);
//			}
//			if (m instanceof UserRelationDTO) {
//				Type t = new TypeReference<List<UserDTO>>() {}.getType();
//				UserRelationDTO ur = (UserRelationDTO) m;
//				ur.setFrom((UserDTO)mapping.get(t.toString()
//							+ ur.getFrom().getId()));
//				ur.setTo((UserDTO)mapping.get(t.toString()
//							+ ur.getTo().getId()));
//			}
			gr.persist(m);

			mapping.put(type.toString() + m.getId(), m);
			i++;
		}
		System.out.println(l.get(0).getClass().getSimpleName()
				+ " initialized with " + i + " entities");
	}

}
