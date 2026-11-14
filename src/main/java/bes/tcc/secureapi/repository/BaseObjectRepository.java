package bes.tcc.secureapi.repository;

import bes.tcc.secureapi.dto.MyObject;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BaseObjectRepository {
	private final JdbcTemplate jdbcTemplate;

	public BaseObjectRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Optional<MyObject> findById(long id) {
		List<MyObject> objects = jdbcTemplate.query(
				connection -> {
					PreparedStatement statement =
							connection.prepareStatement("SELECT id, name FROM base_objects WHERE id = ?");
					statement.setLong(1, id);
					return statement;
				},
				(resultSet, rowNumber) -> new MyObject(resultSet.getLong("id"), resultSet.getString("name")));
		return objects.stream().findFirst();
	}
}
