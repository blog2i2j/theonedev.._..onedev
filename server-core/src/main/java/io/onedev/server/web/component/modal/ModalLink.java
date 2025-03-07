package io.onedev.server.web.component.modal;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

public abstract class ModalLink extends AjaxLink<Void> {

	private ModalPanel modal;
	
	public ModalLink(String id) {
		super(id);
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		// if modal has not been created, or has been removed from page 
		// when the same page instance is refreshed 
		if (modal == null || modal.getParent() == null) {
			modal = new ModalPanel(target) {
	
				@Override
				protected Component newContent(String id) {
					return ModalLink.this.newContent(id, this);
				}

				@Override
				protected void onClosed() {
					super.onClosed();
					modal = null;
				}

				@Override
				protected String getCssClass() {
					return getModalCssClass();
				}
				
			};
		}
	}

	protected String getModalCssClass() {
		return "modal-lg";
	}
	
	protected abstract Component newContent(String id, ModalPanel modal);
}
