package io.onedev.server.web.component.codecomment;

import static io.onedev.server.search.entity.codecomment.CodeCommentQueryLexer.IsSince;
import static io.onedev.server.search.entity.codecomment.CodeCommentQueryLexer.IsUntil;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
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
import io.onedev.server.entitymanager.UserManager;
import io.onedev.server.model.CodeComment;
import io.onedev.server.model.User;
import io.onedev.server.search.entity.EntityQuery;
import io.onedev.server.search.entity.codecomment.CreatedByCriteria;
import io.onedev.server.search.entity.codecomment.CreatedByUserCriteria;
import io.onedev.server.search.entity.codecomment.LastActivityDateCriteria;
import io.onedev.server.search.entity.codecomment.ResolvedCriteria;
import io.onedev.server.search.entity.codecomment.StatusCriteria;
import io.onedev.server.search.entity.codecomment.UnresolvedCriteria;
import io.onedev.server.util.DateUtils;
import io.onedev.server.util.criteria.Criteria;
import io.onedev.server.web.component.datepicker.DatePicker;
import io.onedev.server.web.component.filteredit.FilterEditPanel;
import io.onedev.server.web.component.stringchoice.StringSingleChoice;
import io.onedev.server.web.component.user.choice.UserMultiChoice;

class CodeCommentFilterPanel extends FilterEditPanel<CodeComment> {
	
	public CodeCommentFilterPanel(String id, IModel<EntityQuery<CodeComment>> queryModel) {
		super(id, queryModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

        var statusChoice = new StringSingleChoice("status", new IModel<String>() {

			@Override
			public void detach() {
			}

			@Override
			public String getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), StatusCriteria.class, null);
				if (criterias.size() == 0)
					return null;
				else
					return criterias.get(0) instanceof UnresolvedCriteria ? "Unresolved" : "Resolved";
			}

			@Override
			public void setObject(String object) {					
				Criteria<CodeComment> rootCriteria;
				if (object == null)
					rootCriteria = null;
				else if (object.equals("Unresolved"))
					rootCriteria = new UnresolvedCriteria();
				else
					rootCriteria = new ResolvedCriteria();
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), StatusCriteria.class, rootCriteria, null));
				getModel().setObject(query);
			}

		}, new LoadableDetachableModel<Map<String, String>>() {

			@Override
			protected Map<String, String> load() {
				var map = new LinkedHashMap<String, String>();
				map.put("Unresolved", "Unresolved");
				map.put("Resolved", "Resolved");
				return map;
			}
		}, false);
		statusChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(statusChoice);
	
		var createdByChoice = new UserMultiChoice("createdBy", new IModel<Collection<User>>() {

			@Override
			public void detach() {
			}

			@Override
			public Collection<User> getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), CreatedByCriteria.class, null);
				return criterias.stream().map(it->it.getUser()).filter(Objects::nonNull).collect(toList());
			}

			@Override
			public void setObject(Collection<User> object) {	
				var criteria = Criteria.orCriterias(object.stream().map(it->new CreatedByUserCriteria(it)).collect(toList()));
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), CreatedByUserCriteria.class, criteria, null));
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
		createdByChoice.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});			
		add(createdByChoice);

		var activeSincePicker = new DatePicker("activeSince", new IModel<Date>() {

			@Override
			public void detach() {
			}

			private Predicate<LastActivityDateCriteria> getPredicate() {
				return t -> t.getOperator() == IsSince;
			}

			@Override
			public Date getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), LastActivityDateCriteria.class, getPredicate());
				if (criterias.isEmpty())
					return null;
				else
					return criterias.get(0).getDate();
			}

			@Override
			public void setObject(Date object) {
				var criteria = object!=null? new LastActivityDateCriteria(object, DateUtils.formatDate(object), IsSince) : null;
				var query = getModelObject();
				query.setCriteria(setMatchingCriteria(query.getCriteria(), LastActivityDateCriteria.class, criteria, getPredicate()));
				getModel().setObject(query);
			}

		}, false);
		activeSincePicker.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(activeSincePicker);

		var notActiveSincePicker = new DatePicker("notActiveSince", new IModel<Date>() {

			@Override
			public void detach() {
			}

			private Predicate<LastActivityDateCriteria> getPredicate() {
				return t -> t.getOperator() == IsUntil;
			}

			@Override
			public Date getObject() {
				var criterias = getMatchingCriterias(getModelObject().getCriteria(), LastActivityDateCriteria.class, getPredicate());
				if (criterias.isEmpty())
					return null;
				else
					return criterias.get(0).getDate();
			}

			@Override
			public void setObject(Date object) {
				var criteria = object!=null? new LastActivityDateCriteria(object, DateUtils.formatDate(object), IsUntil) : null;
				var query = getModelObject();	
				query.setCriteria(setMatchingCriteria(query.getCriteria(), LastActivityDateCriteria.class, criteria, getPredicate()));
				getModel().setObject(query);
			}

		}, false);
		notActiveSincePicker.add(new AjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
			}
			
		});
		add(notActiveSincePicker);				
	}
	
	private static UserManager getUserManager() {
		return OneDev.getInstance(UserManager.class);
	}

}
