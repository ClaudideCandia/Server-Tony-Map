package src.database;

import src.exceptions.DatabaseConnectionException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * La classe TableSchema rappresenta lo schema di una tabella in un database relazionale.
 * Fornisce informazioni sulle colonne della tabella, come il nome e il tipo di dati,
 * utilizzando una connessione al database fornita da {@link DbAccess}.
 */
public class TableSchema {
	private DbAccess db;

	/**
	 * La classe interna Column rappresenta una colonna di una tabella con il nome e il tipo di dati.
	 */
	public class Column {
		private String name;
		private String type;

		/**
		 * Costruttore per la creazione di una nuova colonna.
		 *
		 * @param name Nome della colonna.
		 * @param type Tipo di dato della colonna (ad esempio, "string" o "number").
		 */
		Column(String name, String type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * Restituisce il nome della colonna.
		 *
		 * @return Nome della colonna.
		 */
		public String getColumnName() {
			return name;
		}

		/**
		 * Determina se il tipo di dati della colonna è un numero.
		 *
		 * @return {@code true} se il tipo è "number", altrimenti {@code false}.
		 */
		public boolean isNumber() {
			return type.equals("number");
		}

		/**
		 * Restituisce una rappresentazione testuale della colonna.
		 *
		 * @return Stringa contenente il nome e il tipo della colonna.
		 */
		public String toString() {
			return name + ":" + type;
		}
	}

	/**
	 * Lista delle colonne che compongono lo schema della tabella.
	 */
	List<Column> tableSchema = new ArrayList<Column>();

	/**
	 * Costruttore che inizializza lo schema di una tabella specificata recuperando
	 * i metadati del database.
	 *
	 * @param db Oggetto {@link DbAccess} per accedere al database.
	 * @param tableName Nome della tabella di cui creare lo schema.
	 * @throws SQLException Se si verifica un errore durante l'accesso ai metadati della tabella.
	 * @throws DatabaseConnectionException Se la connessione al database non è valida.
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException, DatabaseConnectionException {
		this.db = db;
		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
		// http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");

		Connection con = db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);

		while (res.next()) {
			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
				tableSchema.add(new Column(
						res.getString("COLUMN_NAME"),
						mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
		}
		res.close();
	}

	/**
	 * Restituisce il numero di colonne nello schema della tabella.
	 *
	 * @return Numero di colonne presenti nello schema.
	 */
	public int getNumberOfAttributes() {
		return tableSchema.size();
	}

	/**
	 * Restituisce la colonna in base all'indice specificato.
	 *
	 * @param index Indice della colonna da recuperare.
	 * @return Oggetto {@link Column} rappresentante la colonna all'indice specificato.
	 */
	public Column getColumn(int index) {
		return tableSchema.get(index);
	}
}


		     


