
package org.luwrain.app.chat;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.Popups;
import org.luwrain.controls.*;

class ChatApp implements Application, MonoApp, TelegramAccountListener
{
    private Luwrain luwrain;
    private final Base base = new Base();
    private Actions actions;
    private Strings strings;

    private TreeArea treeArea;
    private ChatArea chatArea;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	if (!base.init(luwrain, this))
	    return false;
	actions = new Actions(luwrain);
	createArea();
	return true;
    }

    private void createArea()
    {
	final TreeArea.Params treeParams = new TreeArea.Params();
	treeParams.environment = new DefaultControlEnvironment(luwrain);
	treeParams.model = base.getTreeModel();
	treeParams.name = strings.sectionsAreaName();
	//treeParams.clickHandler = (area, obj)->openSection(obj);

	treeArea = new TreeArea(treeParams){

		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.isSpecial() && !event.isModified())
			switch (event.getSpecial())
			{
			case TAB:
			    return gotoChatArea();
			}
		    return super.onKeyboardEvent(event);
		}

		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onEnvironmentEvent(event);
		    switch (event.getCode())
		    {
		    case ACTION:
			return onTreeAction(event);
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}

		@Override public Action[] getAreaActions()
		{
		    return actions.getTreeActions();
		}
	};

chatArea = new ChatArea(new DefaultControlEnvironment(luwrain)) {

	    @Override public boolean onKeyboardEvent(KeyboardEvent event)
	    {
		NullCheck.notNull(event, "event");
		if (event.isSpecial() && !event.isModified())
		    switch(event.getSpecial())
		{
		case TAB:
		    return gotoTreeArea();
		}
		return super.onKeyboardEvent(event);
	    }

	    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
	    {
		NullCheck.notNull(event, "event");
		if (event.getType() == EnvironmentEvent.Type.REGULAR)
		    return super.onEnvironmentEvent(event);
		switch(event.getCode())
		{
		case CLOSE:
		    closeApp();
		    return true;
		default:
		    return super.onEnvironmentEvent(event);
		}
	    }
	};

chatArea.setEnteringPrefix("proba>");
chatArea.setListener((text)->chatArea.addLine("entered>", text));

		//		base.init();
    }

    private boolean onTreeAction(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (ActionEvent.isAction(event, "add-account"))
	    return actions.onAddAccount(treeArea);
	if (ActionEvent.isAction(event, "select-item"))
	    return actions.onSelectItem(treeArea, chatArea);
	if (ActionEvent.isAction(event, "add-contact"))
	    return actions.onAddContact(treeArea, chatArea);
	return false;
    }

    @Override public void onNewMessage()
    {
	luwrain.onAreaNewContent(chatArea);
    }

    @Override public void onUnknownContactReciveMessage(String message)
    {
	luwrain.message("Неизвестный контакт: "+message);
	luwrain.onAreaNewContent(treeArea);
    }

    /*
private void init()
	{
		Object[] accounts=treeModelSource.getChildObjs(treeModelSource.getRoot());
		for(Object o:accounts)
		{
			Account a=(Account)o;
			a.doAutoConnect(new Runnable()
			{
				@Override public void run()
				{
					sectionsArea.refresh();
				}
			});
		}
		}
    */

    private boolean gotoTreeArea()
    {
	luwrain.setActiveArea(treeArea);
	return true;
    }

    private boolean gotoChatArea()
    {
	luwrain.setActiveArea(chatArea);
	return true;
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT, treeArea, chatArea);
    }

    @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }

private void closeApp()
    {
	luwrain.closeApp();
    }
}
