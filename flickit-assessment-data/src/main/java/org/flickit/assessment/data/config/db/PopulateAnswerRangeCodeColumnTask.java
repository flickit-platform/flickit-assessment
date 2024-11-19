package org.flickit.assessment.data.config.db;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.flickit.assessment.common.util.GenerateCodeUtil.generateCode;

public class PopulateAnswerRangeCodeColumnTask implements CustomTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        JdbcConnection connection = (JdbcConnection) database.getConnection();
        try {
            String selectQuery = "SELECT id, title FROM fak_answer_range WHERE reusable IS TRUE";
            String updateQuery = "UPDATE fak_answer_range SET code = ? WHERE id = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                 PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                ResultSet resultSet = selectStmt.executeQuery();

                while (resultSet.next()) {
                    updateCode(resultSet, updateStmt);
                }
            }
        } catch (Exception e) {
            throw new CustomChangeException("Error populating fac_answer_range code column", e);
        }
    }

    private void updateCode(ResultSet resultSet, PreparedStatement updateStmt) throws SQLException {
        long id = resultSet.getLong("id");
        String title = resultSet.getString("title");
        String code = generateCode(title);

        updateStmt.setString(1, code);
        updateStmt.setLong(2, id);
        updateStmt.executeUpdate();
    }

    @Override
    public String getConfirmationMessage() {
        return "code column updated for fac_answer_range existing rows.";
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
