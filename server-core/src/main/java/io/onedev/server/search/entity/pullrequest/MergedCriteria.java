package io.onedev.server.search.entity.pullrequest;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.onedev.server.model.PullRequest;
import io.onedev.server.model.PullRequest.Status;
import io.onedev.server.util.ProjectScope;

public class MergedCriteria extends StatusCriteria {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<PullRequest, PullRequest> from, CriteriaBuilder builder) {
		return builder.equal(from.get(PullRequest.PROP_STATUS), Status.MERGED);
	}

	@Override
	public boolean matches(PullRequest request) {
		return request.isMerged();
	}

	@Override
	public String toStringWithoutParens() {
		return PullRequestQuery.getRuleName(PullRequestQueryLexer.Merged);
	}

	@Override
	public Status getStatus() {
		return Status.MERGED;
	}

}
