import dbworking.*
import java.io.IOException

class DBCommander {
    private val container = HashMap<String, Any>()
    private val cmdList = "Список с описанием доступных команд:\n" +
            "'stop' - завершает работу программы\n" +
            "'dblist' - возвращает список бд, доступных на сервере\n" +
            "'connect' - процедура подключения к серверу\n" +
            "'reconnect' - процедура переподключения к серверу\n" +
            "'disconnect' - отключиться от сервера\n" +
            "'create table' - процедура создания и заполнения таблиц в бд через файл .xlsx\n" +
            "'create database' - создать БД\n" +
            "'delete database' - удалить БД\n" +
            "'use' - выбор бд\n" +
            "'delete' - удаление выбранного бд\n"

    init {
        Communicator.writeLine("Запуск...")
        Communicator.writeLine("Ожидание команды")
        while (true){
            when (Communicator.readLine("-> ").trim().toLowerCase()){
                "stop" -> {
                    stop()
                    break
                }
                "dblist" ->{
                    getDatabases()
                }
                "connect" ->{
                    connect()
                }
                "reconnect"->{
                    reconnect()
                }
                "disconnect" -> {
                    disconnect()
                }
                "help" -> {
                    Communicator.writeLine(cmdList)
                }
                "create database"->{
                    createDB()
                }
                "create table"->{
                    createTables()
                }
                "use"->{
                    useDB()
                }
                "delete database"->{
                    deleteDB()
                }
                else -> {
                    Communicator.writeLine("Вы ничего не ввели :)")
                }
            }
        }
        Communicator.writeLine("Завершение работы...")
    }

    private fun deleteDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.deleteDataBase(Communicator.readLine("Введите имя базы данных для удаления: "))
    }

    private fun useDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.useDataBase(Communicator.readLine("Введите имя базы данных для выбора: "))
    }

    private fun createDB(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        dbh.createDataBase(Communicator.readLine("Введите имя базы данных: "))
    }

    private fun stop(){
        if(container.containsKey("server")){
            val server = container["server"] as SQLServer
            server.closeConnection()
        }
    }

    private fun disconnect(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Чтобы отключиться, нужно для начала подключиться :)")
            return
        }
        val server = container["server"] as SQLServer
        server.closeConnection()
        container.remove("server")
        Communicator.writeLine("Успешное отключение от сервера!")
    }

    private fun reconnect(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Вы не подключены ни к какому серверу!")
            return
        }
        val server = container["server"] as SQLServer
        val host = Communicator.readLine("Введите имя хоста (по умолчанию 'localhost')\n-> ").trim()
        val port = Communicator.readLine("Введите порт (по умолчанию '3306')\n-> ").trim()
        val username = Communicator.readLine("Введите имя пользователя (по умолчанию 'root')\n-> ").trim()
        val password = Communicator.readLine("Введите пароль (по умолчанию 'root')\n-> ").trim()
        server.resetHost(if(host.isBlank()) "localhost" else host)
        server.resetPort(if(port.isBlank() || port.toIntOrNull() == null) 3306 else port.toInt())
        server.resetUsername(if(username.isBlank()) "root" else username)
        server.resetPassword(if(password.isBlank()) "root" else password)
        if(server.connect()){
            Communicator.writeLine("Вы успешно переподключились к серверу: ${server.sHost}: ${server.sPort}")
        }
    }

    private fun connect(){
        if(container.containsKey("server")){
            val server = container["server"] as SQLServer
            if(!server.isConnected){
                if(!server.connect()){
                    Communicator.writeLine("Не удается подключиться к серверу :(")
                    Communicator.writeLine("Попробуйте команду 'reconnect' для переподключения")
                }
            }
            Communicator.writeLine("Вы уже подключены к серверу: ${server.sHost}: ${server.sPort}")
            return
        }
        val host = Communicator.readLine("Введите имя хоста (по умолчанию 'localhost')\n-> ").trim()
        val port = Communicator.readLine("Введите порт (по умолчанию '3306')\n-> ").trim()
        val username = Communicator.readLine("Введите имя пользователя (по умолчанию 'root')\n-> ").trim()
        val password = Communicator.readLine("Введите пароль (по умолчанию 'root')\n-> ").trim()
        container["server"] = MySQLServer(
            if(host.isBlank()) "localhost" else host,
            if(port.isBlank() || port.toIntOrNull() == null) 3306 else port.toInt(),
            if(username.isBlank()) "root" else username,
            if(password.isBlank()) "root" else password
        )
    }

    private fun getDatabases(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Для данной команды требуется подключение к серверу")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует подключение к серверу :(")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        val dbh = container["dbh"] as DBHelper
        Communicator.writeLine("")
        Communicator.writeLine(dbh.displayDBs())
    }

    private fun createTables(){
        if(!container.containsKey("server")){
            Communicator.writeLine("Подключитесь к серверу!")
            return
        }
        val server = container["server"] as SQLServer
        if(!server.isConnected){
            Communicator.writeLine("Отсутствует соединение с сервером!")
            return
        }
        if(!container.containsKey("dbh")){
            container["dbh"] = DBHelper(server)
        }
        if(!container.containsKey("tables")){
            container["tables"] = ExcelTableWorker()
        }
        if(!container.containsKey("data")){
            container["data"] = ExcelDataWorker()
        }
        val tabs = container["tables"] as DataWorker
        val data = container["data"] as DataWorker
        val dbh = container["dbh"] as DBHandler
        val pth = Communicator.readLine("Введите путь к файлу .xlsx: ")
        try {
            tabs.getDataFrom(pth)
            tabs.loadDataTo(dbh)
            data.getDataFrom(pth)
            data.loadDataTo(dbh)
        }catch (ex: IOException){
            Communicator.writeLine("Не удалось найти файл!")
        }
    }
}