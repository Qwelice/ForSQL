import dbworking.*
import java.io.IOException

class DBCommander {
    private val cmdList = "Список с описанием доступных команд:\n" +
            "'stop' - завершает работу программы\n" +
            "'dblist' - возвращает список бд, доступных на сервере\n" +
            "'connect' - процедура подключения к серверу\n" +
            "'reconnect' - процедура переподключения к серверу\n" +
            "'disconnect' - отключиться от сервера\n" +
            "'create tables' - процедура создания и заполнения таблиц в бд через файл .xlsx\n" +
            "'create database' - создать БД\n" +
            "'delete database' - удалить БД\n" +
            "'use' - выбор бд\n" +
            "'delete' - удаление выбранного бд\n"

    init {
        val adapter = DBAdapter()
        Communicator.writeLine("Запуск...")
        Communicator.writeLine("Ожидание команды")
        while (true){
            when (Communicator.readLine("-> ").trim().toLowerCase()){
                "stop" -> {
                    adapter.stop()
                    break
                }
                "dblist" ->{
                    adapter.getDatabases()
                }
                "connect" ->{
                    adapter.connect()
                }
                "reconnect"->{
                    adapter.reconnect()
                }
                "disconnect" -> {
                    adapter.disconnect()
                }
                "help" -> {
                    Communicator.writeLine(cmdList)
                }
                "create database"->{
                    adapter.createDB()
                }
                "create tables"->{
                    adapter.createTables()
                }
                "use"->{
                    adapter.useDB()
                }
                "delete database"->{
                    adapter.deleteDB()
                }
                else -> {
                    Communicator.writeLine("Вы ничего не ввели :)")
                }
            }
        }
        Communicator.writeLine("Завершение работы...")
    }
}