package dao;

import dao.dto.UserMaxOrderDto;
import dao.error.ConnectionUnavailableException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Mejorar cada uno de los métodos a nivel SQL y código cuando sea necesario
 * Razonar cada una de las mejoras que se han implementado
 * No es necesario que el código implementado funcione
 */
public class TestSqlDao {

    private static final TestSqlDao instance = new TestSqlDao();

    // Esta variable no deberia ser de clase, solo se usa en el metodo 'getMaxUserOrderId' por lo que
    // se puede declarar directamente en el metodo. Se deberia borrar la linea siguiente,donde se declara
    // como variable de clase.
    private Hashtable<Long, Long> maxOrderUser;

    // Este constructor es privado. Si se planea usar un patron de inyección de dependencia deberia
    // cambiarse a publico
    private TestSqlDao() {
    }

    // Dado que el constructor de TestSqlDao es privado y la única forma de obtener una instancia es a través
    // del método getInstance(), este metodo debe ser public0. De esta forma se obtiene una instancia singleton.
    public static TestSqlDao getInstance() {
        return instance;
    }

    /**
     * Obtiene el ID del último pedido para cada usuario
     */
    // Es recomendable devolver un tipo más abstracto, como Map, en lugar de una implementación específica (Hashtable).
    // Esto permite flexibilidad al elegir diferentes implementaciones del método en lugar de dependencia directa a una
    // implementación concreta y ayuda a reducir el acoplamiento.
    public Map<Long, Long> getMaxUserOrderId(long idTienda) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Utilizamos try-catch-finally para asegurar la liberación adecuada de recursos en el bloque finally
        // para asegurarnos de que se cierran incluso si ocurre una excepción. Si usaramos una version Java 7
        // o superior, usariamos try-with-resources donde los recursos se declaran dentro del bloque try, y se
        // cierran automáticamente cuando se sale del mismo, ya sea de forma normal o debido a una excepción.
        try {
            connection = getConnection();
            // En lugar de concatenar los valores directamente en la cadena de consulta, introducimos la
            // variable utilizando sentencias preparadas para prevenir vulnerabilidades de inyección SQL.
            String query = "SELECT ID_PEDIDO, ID_USUARIO FROM PEDIDOS WHERE ID_TIENDA = ?;";
            stmt = connection.prepareStatement(query);
            stmt.setLong(1, idTienda);
            rs = stmt.executeQuery();

            // En lugar de usar Hashtable, podemos utilizar HashMap que proporciona una implementación más eficiente.
            // La clase Hashtable es segura para multithreading, pero si no necesitamos sincronización obtenemos un
            // mejor rendimiento con HashMap.
            Map<Long, Long> maxOrderUser = new HashMap<Long, Long>();

            while (rs.next()) {
                long idPedido = rs.getInt("ID_PEDIDO");
                long idUsuario = rs.getInt("ID_USUARIO");

                // Juntamos los dos if en una sola condicion para simplificar y eliminar la duplicidad de código.
                if (!maxOrderUser.containsKey(idUsuario) ||
                        maxOrderUser.get(idUsuario) < idPedido) {
                    maxOrderUser.put(idUsuario, idPedido);
                }
            }

            return maxOrderUser;
        } catch (IOException e) {
            throw new ConnectionUnavailableException(e.getLocalizedMessage());
        } finally {
            // Cerramos los recursos de la base de datos en el bloque finally para asegurar que siempre se
            // liberen, incluso en caso de excepciones. Este bloque finally explícito no seria necesario
            // con el uso del try-with-resources antes mencionado.
            if (connection != null) {
                connection.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Copia todos los pedidos de un usuario a otro
     */
    public void copyUserOrders(long idUserOri, long idUserDes) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        PreparedStatement stmt2 = null;
        ResultSet rs = null;

        // Utilizamos try-catch-finally para asegurar la liberación adecuada de recursos en el bloque finally
        // para asegurarnos de que se cierran incluso si ocurre una excepción. Si usaramos una version Java 7
        // o superior, usariamos try-with-resources donde los recursos se declaran dentro del bloque try, y se
        // cierran automáticamente cuando se sale del mismo, ya sea de forma normal o debido a una excepción.
        try {
            connection = getConnection();
            // En lugar de concatenar los valores directamente en la cadena de consulta, introducimos las
            // variables utilizamos sentencias preparadas para prevenir vulnerabilidades de inyección SQL
            // tanto en esta query como en la de Update.
            String selectQuery = "SELECT FECHA, TOTAL, SUBTOTAL, DIRECCION FROM PEDIDOS WHERE ID_USUARIO = ?;";
            stmt = connection.prepareStatement(selectQuery);
            stmt.setLong(1, idUserOri);
            rs = stmt.executeQuery();

            // Usamos una sentencia Update para actualizar el usuario destino, ya que inicialmente se usa una
            // sentencia Insert para crear una nueva entrada en base de datos sin tener en cuenta la variable idUserDes.
            String updateQuery = "UPDATE PEDIDOS SET FECHA = ?, TOTAL = ?, SUBTOTAL = ?, DIRECCION = ? WHERE ID_USUARIO = ?";
            // Creamos una única conexión a la base de datos fuera del bucle para mejorar el rendimiento.
            // Reutilizar la conexión reducirá la sobrecarga de establecer una nueva conexión en cada iteración
            // especialmente cuando la respuesta devuelve un numero considerable de valores.
            connection.setAutoCommit(false);
            stmt2 = connection.prepareStatement(updateQuery);
            stmt2.setLong(5, idUserDes);

            while (rs.next()) {
                stmt2.setTimestamp(1, rs.getTimestamp("FECHA"));
                stmt2.setDouble(2, rs.getDouble("TOTAL"));
                stmt2.setDouble(3, rs.getDouble("SUBTOTAL"));
                stmt2.setString(4, rs.getString("DIRECCION"));

                stmt2.executeUpdate();
            }
            // Hacer commit de la transacción después de cada iteración del bucle puede afectar el rendimiento
            // y no es necesario en este contexto. Sacamos la instrucción 'connection.commit()' fuera del bucle.
            // De esta manera, el conjunto completo de operaciones de inserción se tratará como una única
            // transacción y el commit se realizará solo una vez cuando el bucle haya terminado de ejecutarse.
            connection.commit();
        } catch (IOException e) {
            throw new ConnectionUnavailableException(e.getLocalizedMessage());
        } finally {
            // Cerramos los recursos de la base de datos en el bloque finally para asegurar que siempre se
            // liberen, incluso en caso de excepciones. Este bloque finally explícito no seria necesario
            // con el uso del try-with-resources antes mencionado.
            if (connection != null) {
                connection.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (stmt2 != null) {
                stmt2.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * Obtiene los datos del usuario y pedido con el pedido de mayor importe para la tienda dada
     */
    // Utilizamos un dto para almacenar y devolver los valores encontrados, ya que los parámetros de entrada
    // se actualizan pero no se usan posteriormente por lo que se pueden omitir. Ademas no es recomendable
    // tener una gran cantidad de parámetros de entrada ya que puede hacer dificil la comprensión y aumentar
    // la posibilidad de errores al llamar al método.
    public UserMaxOrderDto getUserMaxOrder(long idTienda) throws Exception {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // Utilizamos try-catch-finally para asegurar la liberación adecuada de recursos en el bloque finally
        // para asegurarnos de que se cierran incluso si ocurre una excepción. Si usaramos una version Java 7
        // o superior, usariamos try-with-resources donde los recursos se declaran dentro del bloque try, y se
        // cierran automáticamente cuando se sale del mismo, ya sea de forma normal o debido a una excepción.
        try {
            connection = getConnection();
            // En lugar de concatenar los valores directamente en la cadena de consulta, introducimos la
            // variable utilizando sentencias preparadas para prevenir vulnerabilidades de inyección SQL.
            String query = "SELECT U.ID_USUARIO, P.ID_PEDIDO, P.TOTAL, U.NOMBRE, U.DIRECCION " +
                    "FROM PEDIDOS AS P " +
                    "INNER JOIN USUARIOS AS U " +
                    "ON P.ID_USUARIO = U.ID_USUARIO WHERE P.ID_TIENDA = ?;";
            stmt = connection.prepareStatement(query);
            stmt.setLong(1, idTienda);
            rs = stmt.executeQuery();

            double total = 0;
            int userId = 0;
            int orderId = 0;
            String name = "";
            String address = "";

            while (rs.next()) {
                if (rs.getLong("TOTAL") > total) {
                    total = rs.getLong("TOTAL");
                    userId = rs.getInt("ID_USUARIO");
                    orderId = rs.getInt("ID_PEDIDO");
                    name = rs.getString("NOMBRE");
                    address = rs.getString("DIRECCION");
                }
            }
            // Usamos el patrón Builder para simplificar la construcción de objetos inmutables y evitando
            // tener un constructor con múltiples parámetros.
            return new UserMaxOrderDto.Builder()
                    .userId(userId)
                    .orderId(orderId)
                    .name(name)
                    .address(address)
                    .build();
        } catch (IOException e) {
            throw new ConnectionUnavailableException(e.getLocalizedMessage());
        } finally {
            // Cerramos los recursos de la base de datos en el bloque finally para asegurar que siempre se
            // liberen, incluso en caso de excepciones. Este bloque finally explícito no seria necesario
            // con el uso del try-with-resources antes mencionado.
            if (connection != null) {
                connection.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    private Connection getConnection() throws IOException {
        // Recuperamos las propiedades de conexion desde un fichero de configuracion  y devolvemos una
        // conecxion JDBC evitando una excepcion por null pointer
        FileReader fileReader = null;
        try {
            Properties properties = new Properties();
            File file = new File("\\config.properties");
            fileReader = new FileReader(file);
            properties.load(fileReader);

            String driver = properties.getProperty("driver");
            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");
            Class.forName(driver);

            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            // Creamos una excepcion ConnectionUnavailableException especifica para estos catch. Si
            // usaramos una version Java 7 o superior, combinariamos multiples excepciones en un solo
            // bloque catch mediante el operador pipe '|'.
            throw new ConnectionUnavailableException(e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            throw new ConnectionUnavailableException(e.getLocalizedMessage());
        } finally {
            if (fileReader != null) {
                fileReader.close();
            }
        }
    }
}
