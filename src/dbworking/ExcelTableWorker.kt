package dbworking

import DataWorker
import Communicator
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.lang.StringBuilder

class ExcelTableWorker : DataWorker {
    private val tables = HashMap<String, String>()

    override fun getDataFrom(path: String) {
        var wb: XSSFWorkbook
        var pth = path.trim()
        while (true){
            try {
                wb = XSSFWorkbook(pth)
                break
            }catch (ex: IOException){
                Communicator.writeLine("По данному пути не существует нужного файла")
                pth = Communicator.readLine("Введите путь к файлу: ").trim()
            }
        }
        val sb = StringBuilder()
        wb.sheetIterator().forEach {
            sb.clear()
            it.getRow(0).cellIterator().forEach { that ->
                if(that.stringCellValue.isNotBlank() || that != null){
                    sb.append("${that.stringCellValue};")
                }
            }
            tables[it.sheetName] = sb.toString().split(";").toMutableList().also { fields ->
                fields.removeIf { that -> that.isBlank() }
            }.joinToString(";")
        }
    }

    override fun loadDataTo(source: Any) {
        if(tables.isEmpty()){
            Communicator.writeLine("Нечего добавлять! Загрузите данные!")
            return
        }
        val dbh = source as DBHandler
        Communicator.writeLine("Для каждого поля таблиц выберите необходимые настройки:")
        tables.keys.forEach {
            Communicator.writeLine("$it : ${tables[it]}")
        }
        tables.keys.forEach { tabName ->
            val fields = correctFields(tables[tabName].toString().split(";").toList())
            dbh.createTable(tabName, fields)
        }
    }

    private fun correctFields(fields: List<String>) : String{
        val attributes = HashMap<String, String>().apply {
            this["INNP"] = "INT NOT NULL PRIMARY KEY"
            this["INN"] = "INT NOT NULL"
            this["IN"] = "INT NULL"
            this["V70N"] = "VARCHAR(70) NULL"
            this["V30N"] = "VARCHAR(30) NULL"
            this["V70NN"] = "VARCHAR(70) NOT NULL"
            this["V30NN"] = "VARCHAR(30) NOT NULL"
            this["V70NNP"] = "VARCHAR(70) NOT NULL PRIMARY KEY"
            this["V30NNP"] = "VARCHAR(30) NOT NULL PRIMARY KEY"
            this["DNN"] = "DATE NOT NULL"
            this["DN"] = "DATE NULL"
        }
        val sb = StringBuilder()
        val primary = mutableListOf<String>()
        fields.forEach {
            sb.append("`$it` ")
            while (true){
                try {
                    val attrID = Communicator.readLine("Введите ID нужного набора: ").trim().toUpperCase()
                    if(attributes.keys.contains(attrID)){
                       val attr = attributes[attrID].toString()
                        if(attr.contains("PRIMARY KEY")){
                            primary.add(it)
                            sb.append("${attr.substringBefore(" PRIMARY KEY")}, ")
                            break
                        }
                        sb.append("$attr, ")
                        break
                    }
                }catch (ex: Exception){
                    Communicator.writeLine("Данного набора не существует")
                }
            }
        }
        sb.append("PRIMARY KEY(")
        for(p in primary){
            if(p == primary.last()){
                sb.append("`$p`)")
                break
            }
            sb.append("`$p`, ")
        }
        return sb.toString()
    }
}