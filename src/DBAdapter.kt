import dbworking.MySQLServer
import dbworking.SQLServer

class DBAdapter {
    private val container = HashMap<String, Any>()

    private val serverExists: Boolean
        get() = container.containsKey("server")

    private fun createMySQLServer(host: String, port: Int, username: String, password: String) : Boolean{
        container["server"] = MySQLServer(host, port, username, password)
        val server = container["server"] as SQLServer
        return server.connect()
    }

    fun connectTo(host: String, port: Int, username: String, password: String) : Boolean{
        if(!serverExists){
            return createMySQLServer(host, port, username, password)
        }
        val server = container["server"] as SQLServer
        server.resetHost(host)
        server.resetPort(port)
        server.resetUsername(username)
        server.resetPassword(password)
        return server.connect()
    }


}