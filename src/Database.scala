import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import scala.collection.mutable.ArrayBuffer



object OrderTracking {

  def main(args: Array[String]): Unit = {
    // connect to the database named "mysql" on the localhost
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost/mydb"
    val username = "root"
    val password = ""
    var connection: Connection = null
    connection = DriverManager.getConnection(url, username, password)

    println("Enter your ID: ")
    val id = scala.io.StdIn.readInt()

    validation(id)

  def validation(x: Int){


    try {
      // make the connection
      Class.forName(driver)
      //connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT * FROM warehouseop where wareOpID = '%s'".format(x))
      while(resultSet.next()){
        val j = ArrayBuffer(resultSet)
        println("Success")
      println("Enter the number of the process would you like to do?" + "\n" + "1. Find and Complete Orders" + "\n" + "2. Add Stock Deliveries")
      val process : Int = scala.io.StdIn.readInt()
      (runCase(process))
    }} catch {

        case _: Throwable =>
    }
  }



    // process directing

    def runCase(x: Int): Unit = x match {
      case 1 => findorders()
      case 2 => updateStock()

    }


    // print list of current orders
    def findorders() {


      try {
        // make the connection
        Class.forName(driver)


        // create the statement, and run the select query
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT orders.status, orders.wareOpID, orders.orderID, products.modelID, products.productType, orders.quantity, " +
          "orders.porus FROM orders INNER JOIN products ON orders.modelID=products.modelID ORDER BY orders.orderID;")

        while (resultSet.next()) {

          print("Status: " + resultSet.getString("status") + "\n" + "Warehouse Op ID: "
            + resultSet.getString("wareOpID") +
            "\n" + "Order ID: " + resultSet.getString("orderID") +
            "\n" + "Model ID: " +  resultSet.getString("modelID") + "\n" +
            "Product Type: " + resultSet.getString("productType") +
            "\n" + "Quantity: " + resultSet.getString("quantity") + "\n" +
            "Porus: " + resultSet.getString("porus") + "\n")
          println(" ")
          }
      } catch {
        case e: Throwable => e.printStackTrace
      }

      selectOrder()

      connection.close()
    }



    // select order
def selectOrder() {


      println("Would you like to work on an order, Enter yes or no")
      val verify = scala.io.StdIn.readLine()


      if(verify == "yes") {

        println("Select an order to process by adding selecting Order ID: ")
        val pro = scala.io.StdIn.readInt()

        var modelArray = ArrayBuffer[Int]()
        var quantityArray = ArrayBuffer[Int]()
        var counter = 0

        try {
          // make the connection
          Class.forName(driver)

          // create the statement, and run the select query
          val statement = connection.createStatement()
          val resultSet = statement.executeQuery("SELECT o.orderID, o.wareOpID, p.modelID, p.productType, " +
            "o.quantity, p.location, o.porus, c.first_name, c.last_name, c.address FROM orders o JOIN products p ON o.modelID = p.modelID JOIN " +
            "customer c ON c.customerID = o.customerID WHERE OrderID='%s'".format(pro))


          while (resultSet.next()) {

            println("Order ID: " + resultSet.getString("orderID"))
            println("Warehouse Operative ID: " + resultSet.getString("wareOpID"))
            val wop = resultSet.getInt("wareOpID")
            println("Model Number: " + resultSet.getString("modelID"))
            modelArray += resultSet.getInt("modelID")
            quantityArray += resultSet.getInt("quantity")

            println("Product Type: " + resultSet.getString("productType"))
            println("Quantity: " + resultSet.getString("quantity"))
            println("Warehouse Location: " + resultSet.getString("location"))
            println("Porous Required: " + resultSet.getString("porus") + "\n")

            //check if member is already active
            if (id != wop) {
              println("Another Warehouse Operative Member is working on it ")

            }
          }
        }catch {
          case e => e.printStackTrace()
        }


       //update order process
        println("What do you want to do with this order?")
        println("1. In Progress")
        println("2. Dispatched")
        println("3. Delivered")
        val r = scala.io.StdIn.readInt()

        try {
          // make the connection

        Class.forName (driver)
          // create the statement, and run the select query
        r match {

            //set status and assign warehouse operative to an order
          case 1 => val statement = connection.createStatement ()
            statement.executeUpdate ("UPDATE orders SET wareOpID='%s', status='in progress' WHERE orderID='%s'".format(id, pro) )
            val stmt = connection.prepareStatement("UPDATE products SET quantity = quantity - ?, hold = hold + ? WHERE modelID = ? and quantity > 0")

            counter = 0
            for(q <- modelArray){
              stmt.setInt(3, q)
              stmt.setInt(1, quantityArray(counter))
              stmt.setInt(2, quantityArray(counter))
              stmt.addBatch()
              counter +1
            }
            stmt.executeBatch()



          // execute travelling salesman algorithm

            //dispatched orders
          case 2 => val statement = connection.createStatement ()
            statement.executeUpdate ("UPDATE orders SET wareOpID='%s', status='dispatched' WHERE orderID='%s'".format (id, pro) )

        //item delivered
          case 3 => val statement = connection.createStatement ()
            statement.executeUpdate ("UPDATE orders SET wareOpID='%s', status='delivered' WHERE orderID='%s'".format (id, pro) )
          case _ => println("incorrect entry")
        }

        } catch {
              case e: Throwable => e.printStackTrace()
            }
        }

       else {
        sys.exit(0)
        println("no")
      }

    }



  def updateStock(){

    println("Please Enter stock details ")
    println("Whats the Model Number: ")
    val model = scala.io.StdIn.readInt()
    println("Whats the Product Type: ")
    val prod = scala.io.StdIn.readLine()
    println("Whats the Quantity: ")
    val quantity  = scala.io.StdIn.readLine()
    println("What is the location in the warehouse")
    val loc = scala.io.StdIn.readLine()


    try {
      // make the connection
      Class.forName(driver)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet  = statement.executeQuery("INSERT INTO products (modelID, productType, location, quantity) VALUES ('%d', '%s', '%s', '%s')".format(model, prod, quantity, loc))

      /*while(resultSet.next()){


      }
*/
    }catch{

      case e => e.printStackTrace()

    }
  }
  }


}