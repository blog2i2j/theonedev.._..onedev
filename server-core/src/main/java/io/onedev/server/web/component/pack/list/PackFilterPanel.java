package io.onedev.server.web.component.pack.list;

import static io.onedev.server.search.entity.pack.PackQueryLexer.Is;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.LabelSpecManager;
import io.onedev.server.entitymanager.ProjectManager;
import io.onedev.server.entitymanager.UserManager;
import io.onedev.server.model.Pack;
import io.onedev.server.model.Project;
import io.onedev.server.model.User;
import io.onedev.server.pack.PackSupport;
import io.onedev.server.search.entity.EntityQuery;
import io.onedev.server.search.entity.pack.LabelCriteria;
import io.onedev.server.search.entity.pack.PackQueryLexer;
import io.onedev.server.search.entity.pack.PublishDateCriteria;
import io.onedev.server.search.entity.pack.PublishedViaProjectCriteria;
import io.onedev.server.search.entity.pack.PublishedByUserCriteria;
import io.onedev.server.search.entity.pack.PublishedByCriteria;
import io.onedev.server.search.entity.pack.TypeCriteria;
import io.onedev.server.security.SecurityUtils;
import io.onedev.server.security.permission.AccessProject;
import io.onedev.server.util.DateUtils;
import io.onedev.server.util.criteria.Criteria;
import io.onedev.server.web.component.datepicker.DatePicker;
import io.onedev.server.web.component.filteredit.FilterEditPanel;
import io.onedev.server.web.component.project.choice.ProjectMultiChoice;
import io.onedev.server.web.component.stringchoice.StringMultiChoice;
import io.onedev.server.web.component.user.choice.UserMultiChoice;

class PackFilterPanel extends FilterEditPanel<Pack> {
	
	public PackFilterPanel(String id, IModel<EntityQuery<Pack>> queryModel) {
		super(id, queryModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

        var typeChoice = new StringMultiChoice("type", new IModel<Collection<String>>() {

			@Override
			public void detach() {
			}

			@Override
			public Collection<String> getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), TypeCriteria.class, null);
				return criterias.stream().map(it->it.getValue()).collect(toList());
			}

			@Override
			public void setObject(Collection<String> object) {					
				var rootCriteria = Criteria.orCriterias(object.stream().map(it->new TypeCriteria(it, Is)).collect(toList()));
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), TypeCriteria.class, rootCriteria, null));
				getModel().setObject(query);
			}

		}, new LoadableDetachableModel<Map<String, String>>() {

			@Override
			protected Map<String, String> load() {
				var map = new LinkedHashMap<String, String>();
				List<PackSupport> packSupports = new ArrayList<>(OneDev.getExtensions(PackSupport.class));
				packSupports.sort(Comparator.comparing(PackSupport::getOrder));
				for (var packSupport: packSupports) {
					map.put(packSupport.getPackType(), packSupport.getPackType());
				}	
				return map;
			}
		}, false);
		typeChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(typeChoice);

        var publishedByProjectChoice = new ProjectMultiChoice("publishedByProject", new IModel<Collection<Project>>() {

			@Override
			public void detach() {
			}

			@Override
			public Collection<Project> getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), PublishedViaProjectCriteria.class, null);
				return criterias.stream().map(it->getProjectManager().findByPath(it.getProjectPath())).filter(Objects::nonNull).collect(toList());
			}

			@Override
			public void setObject(Collection<Project> object) {	
				var criterias = Criteria.orCriterias(object.stream().map(it->new PublishedViaProjectCriteria(it.getPath())).collect(toList()));
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), PublishedViaProjectCriteria.class, criterias, null));
				getModel().setObject(query);
			}

		}, new LoadableDetachableModel<List<Project>>() {

			@Override
			protected List<Project> load() {
				return new ArrayList<>(SecurityUtils.getAuthorizedProjects(new AccessProject()));
			}

		}) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				getSettings().setPlaceholder("");
			}
		};
		
		publishedByProjectChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(publishedByProjectChoice);

		var publishedByUserChoice = new UserMultiChoice("publishedByUser", new IModel<Collection<User>>() {

			@Override
			public void detach() {
			}

			@Override
			public Collection<User> getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), PublishedByCriteria.class, null);
				return criterias.stream().map(it->it.getUser()).filter(Objects::nonNull).collect(toList());
			}

			@Override
			public void setObject(Collection<User> object) {	
				var criteria = Criteria.orCriterias(object.stream().map(it->new PublishedByUserCriteria(it)).collect(toList()));
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), PublishedByUserCriteria.class, criteria, null));
				getModel().setObject(query);
			}

		}, new LoadableDetachableModel<List<User>>() {

			@Override
			protected List<User> load() {
				return getUserManager().query();
			}

		}) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				getSettings().setPlaceholder("");
			}
		};
		publishedByUserChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});			
		add(publishedByUserChoice);

        var labelChoice = new StringMultiChoice("label", new IModel<Collection<String>>() {

			@Override
			public void detach() {
			}

			@Override
			public Collection<String> getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), LabelCriteria.class, null);
				return criterias.stream()
						.map(it->it.getLabelSpec().getName())
						.collect(toList());
			}

			@Override
			public void setObject(Collection<String> object) {	
				var criterias = Criteria.orCriterias(object.stream().map(it->new LabelCriteria(getLabelSpecManager().find(it), Is)).collect(toList()));
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), LabelCriteria.class, criterias, null));
				getModel().setObject(query);
			}

		}, new LoadableDetachableModel<Map<String, String>>() {

			@Override
			protected Map<String, String> load() {
				var names = getLabelSpecManager().query().stream().map(it->it.getName()).collect(toList());
				Collections.sort(names);
				var map = new LinkedHashMap<String, String>();
				for (String name: names) {
					map.put(name, name);
				}
				return map;
			}

		}, false);
		labelChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(labelChoice);		

		var publishedAfterPicker = new DatePicker("publishedAfter", new IModel<Date>() {

			@Override
			public void detach() {
			}

			private Predicate<PublishDateCriteria> getPredicate() {
				return t -> t.getOperator() == PackQueryLexer.IsSince;
			}

			@Override
			public Date getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), PublishDateCriteria.class, getPredicate());
				if (criterias.isEmpty())
					return null;
				else
					return criterias.get(0).getDate();
			}

			@Override
			public void setObject(Date object) {
				var criteria = object!=null? new PublishDateCriteria(DateUtils.formatDate(object), PackQueryLexer.IsSince) : null;
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), PublishDateCriteria.class, criteria, getPredicate()));
				getModel().setObject(query);
			}

		}, false);
		publishedAfterPicker.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(publishedAfterPicker);

		var publishedBeforePicker = new DatePicker("publishedBefore", new IModel<Date>() {

			@Override
			public void detach() {
			}

			private Predicate<PublishDateCriteria> getPredicate() {
				return t -> t.getOperator() == PackQueryLexer.IsUntil;
			}

			@Override
			public Date getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), PublishDateCriteria.class, getPredicate());
				if (criterias.isEmpty())
					return null;
				else
					return criterias.get(0).getDate();
			}

			@Override
			public void setObject(Date object) {
				var criteria = object!=null? new PublishDateCriteria(DateUtils.formatDate(object), PackQueryLexer.IsUntil) : null;
				var query = getModelObject();	
				query.setCriteria(setMatchingCriteria(query.getCriteria(), PublishDateCriteria.class, criteria, getPredicate()));
				getModel().setObject(query);
			}

		}, false);
		publishedBeforePicker.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(publishedBeforePicker);					
	}
	
	private ProjectManager getProjectManager() {
		return OneDev.getInstance(ProjectManager.class);
	}

	private LabelSpecManager getLabelSpecManager() {
		return OneDev.getInstance(LabelSpecManager.class);
	}	

	private UserManager getUserManager() {
		return OneDev.getInstance(UserManager.class);
	}

}
