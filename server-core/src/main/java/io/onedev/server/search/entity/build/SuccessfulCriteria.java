package io.onedev.server.search.entity.build;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import io.onedev.server.model.Build;
import io.onedev.server.model.Build.Status;
import io.onedev.server.util.ProjectScope;

public class SuccessfulCriteria extends StatusCriteria {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		Path<?> attribute = from.get(Build.PROP_STATUS);
		return builder.equal(attribute, Build.Status.SUCCESSFUL);
	}

	@Override
	public boolean matches(Build build) {
		return build.getStatus() == Build.Status.SUCCESSFUL;
	}

	@Override
	public String toStringWithoutParens() {
		return BuildQuery.getRuleName(BuildQueryLexer.Successful);
	}

	@Override
	public Status getStatus() {
		return Build.Status.SUCCESSFUL;
	}

}
