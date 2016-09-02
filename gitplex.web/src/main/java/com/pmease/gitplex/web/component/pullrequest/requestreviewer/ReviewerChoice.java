package com.pmease.gitplex.web.component.pullrequest.requestreviewer;

import java.util.Date;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import com.pmease.commons.wicket.component.select2.SelectToAddChoice;
import com.pmease.gitplex.core.GitPlex;
import com.pmease.gitplex.core.entity.Account;
import com.pmease.gitplex.core.entity.PullRequest;
import com.pmease.gitplex.core.entity.PullRequestReviewInvitation;
import com.pmease.gitplex.core.manager.PullRequestReviewInvitationManager;
import com.pmease.gitplex.core.security.SecurityUtils;
import com.pmease.gitplex.web.component.accountchoice.AccountChoiceResourceReference;

@SuppressWarnings("serial")
public abstract class ReviewerChoice extends SelectToAddChoice<Account> {

	private final IModel<PullRequest> requestModel;
	
	public ReviewerChoice(String id, IModel<PullRequest> requestModel) {
		super(id, new ReviewerProvider(requestModel));
		
		this.requestModel = requestModel;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();

		PullRequest request = requestModel.getObject();
		setVisible(request.isOpen() 
				&& !request.getPotentialReviewers().isEmpty()
				&& SecurityUtils.canModify(request));
	}
                                                                                                                              
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getSettings().setPlaceholder("Select user to add as reviewer...");
		getSettings().setFormatResult("gitplex.accountChoiceFormatter.formatResult");
		getSettings().setFormatSelection("gitplex.accountChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("gitplex.accountChoiceFormatter.escapeMarkup");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new AccountChoiceResourceReference()));
	}
	
	@Override
	protected void onDetach() {
		requestModel.detach();
		
		super.onDetach();
	}

	protected void onSelect(AjaxRequestTarget target, Account user) {
		PullRequest request = requestModel.getObject();
		PullRequestReviewInvitation invitation = null;
		for(PullRequestReviewInvitation each: request.getReviewInvitations()) {
			if (each.getUser().equals(user)) {
				invitation = each;
				break;
			}
		}
		if (invitation == null) {
			invitation = new PullRequestReviewInvitation();
			invitation.setRequest(request);
			invitation.setUser(user);
			request.getReviewInvitations().add(invitation);
		}
		invitation.setStatus(PullRequestReviewInvitation.Status.ADDED_MANUALLY);
		invitation.setDate(new Date());
		if (!request.isNew())
			GitPlex.getInstance(PullRequestReviewInvitationManager.class).save(invitation);
	};
}
