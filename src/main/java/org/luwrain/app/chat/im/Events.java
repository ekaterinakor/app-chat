
package org.luwrain.app.chat.im;

public interface Events
{
    /**
     * Событие вызывается при возникновении ошибки, 
     * после чего требуется повторное выполнение действия его вызвавшего
     * @param message описание ошибки
     */
    void onError(String message);

    void onWarning(String message);

    /**
     * Событие вызывается когда сервер требует кода двухфакторной авторизации
     * @param message необязательное сообщение
     */
    String askTwoPassAuthCode(String message);

    /**
     * Событие вызывается при окончании успешной авторизации
     */

    /**
     * Событие вызывается на получение результата поиска
     */
    void onNewContact(Contact contact);

	void onNewMessage(Message message,Contact recipient);
/** вызывается перед добавлением контактов в список для очитски*/
	void onBeginAddingContact();
    void receiveNewMessage(String message,int date,int userId);
}
