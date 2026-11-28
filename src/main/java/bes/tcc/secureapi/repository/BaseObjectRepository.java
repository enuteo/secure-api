package bes.tcc.secureapi.repository;

import bes.tcc.secureapi.exception.AccessNotAllowedException;
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

	public Optional<MyObject> findByIdVisibleToUser(long id, String username) {
		List<MyObject> objects = jdbcTemplate.query(
				connection -> {
					PreparedStatement statement =
							connection.prepareStatement("SELECT id, name FROM base_objects WHERE id = ?");
					statement.setLong(1, id);
					return statement;
				},
				(resultSet, rowNumber) -> new MyObject(resultSet.getLong("id"), resultSet.getString("name")));
		Optional<MyObject> object = objects.stream().findFirst();
		if (object.isEmpty()) {
			return object;
		}

		Long userId = jdbcTemplate.query(
				connection -> {
					PreparedStatement statement =
							connection.prepareStatement("SELECT id FROM app_users WHERE username = ?");
					statement.setString(1, username);
					return statement;
				},
				(resultSet, rowNumber) -> resultSet.getLong("id"))
				.stream()
				.findFirst()
				.orElseThrow(AccessNotAllowedException::new);

		if (!hasAccess(id, userId)) {
			throw new AccessNotAllowedException();
		}
		return object;
	}

	private boolean hasAccess(long objectId, long userId) {
		List<Boolean> access = jdbcTemplate.query(
				connection -> {
					PreparedStatement statement =
							connection.prepareStatement("""
									WITH RECURSIVE owner_chain(user_id) AS (
										SELECT owner_id
										FROM base_objects
										WHERE id = ?
										UNION
										SELECT hierarchy.manager_id
										FROM user_hierarchy hierarchy
										JOIN owner_chain ancestor ON hierarchy.user_id = ancestor.user_id
									)
									SELECT EXISTS (
										SELECT 1
										FROM owner_chain
										WHERE user_id = ?
									)
									""");
					statement.setLong(1, objectId);
					statement.setLong(2, userId);
					return statement;
				},
				(resultSet, rowNumber) -> resultSet.getBoolean(1));
		return access.stream().findFirst().orElse(false);
	}
}
