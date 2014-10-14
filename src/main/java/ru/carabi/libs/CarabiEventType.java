package ru.carabi.libs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author sasha
 */
public enum CarabiEventType {
	/**
	 * При получании от клиента -- условный ответ, передача через fireEvent без изменений.
	 * Содержимое пакета произвольное.
	 */
	reserved(0),
	/**
	 * При получении от клиента ответ pong, на сервере генерируется по таймеру
	 * с ожиданием так же ответа pong для контроля подключения и продления 
	 * Glassfish-сессии. Содержимое пакета может быть произвольным (традиционное: "PING ПИНГ")
	 */
	ping(1),
	/**
	 * Ответ не генерируется, при получении сигнала раз в 5 минут продливается сессия на Glassfish.
	 * Содержимое пакета может быть произвольным (традиционное: "PONG ПОНГ")
	 */
	pong(2),
	/**
	 * Авторизация в Eventer с помощью зашифрованного токена от Glassfish.
	 * Содержимое пакета: зашифрованный токен, получаемый через EventerService.getEventerToken
	 */
	auth(3),
	/**
	 * Запросить события с сервера. В ответ приходят сообщения запрошенных типов.
	 * Содержимое пакета: JSON-массив типлв пакетов (например, '[10,11]')
	 */
	synch(4),
	/**
	 * Оключить автоматическое получение событий. Сообщения запрошенных
	 * типов будут приходить по таймеру по мере их появления в системе.
	 * Содержимое пакета: JSON-массив типлв пакетов (например, '[10,11]')
	 */
	autosynch(5),
	/**
	 * Отключение autosynch(5) для определёного типа событий.
	 * Содержимое пакета: JSON-массив типлв пакетов (например, '[10,11]')
	 */
	disableSync(6),
	/**
	 * Отключение autosynch(5) для всех типов событий. Содержимое пакета: пустой.
	 */
	disableSyncAll(7),
	/**
	 * Разослать событие другим клиентам, подключенным к серверу.
	 * Содержимое пакета: JSON-объект с названием базы-источника (при наличии),
	 * логина адресата (если нет, но указана база -- сообщение получают все, кто
	 * работает с этой базой),типа и содержимого пакета.
	 * Пример: {"schema":"carabi", "login":"user", "eventcode":0, "message":"test"}
	 * Все сообщения пересылаются клиентам без изменений.
	 */
	fireEvent(8),
	shutdown(9),
	/**
	 * статистические события в общем массиве (формат: JSON с выделенной шапкой)
	 */
	baseEventsTable(10),
	/**
	 * статистические события в общем массиве (формат: JSON, ассоциативный массив)
	 */
	baseEventsList(11),
	chatMessage(12),
	chatMessageRead(13),
	userOnlineEvent(14),
	userOnlineQuery(15),
	chatMessageRemove(16),
	usersRelationsAdd(17),
	usersRelationsRemove(18),
	usersAvatarChange(19),
	error(Short.MAX_VALUE);

	private short code;
	CarabiEventType (int code) {
		if (code != (short)code) {
			throw new IllegalArgumentException("Too big code: " + code);
		}
		this.code = (short)code;
	}

	public short getCode () {
		return code;
	}

	private final static Map<Short, CarabiEventType> codeValueMap = new ConcurrentHashMap<>(16);

	static {
		for (CarabiEventType type: CarabiEventType.values()) {
			codeValueMap.put(type.code, type);
		}
	}
	public static CarabiEventType getTypeByCode(short codeValue) {
		return codeValueMap.get(codeValue);
	}

}
