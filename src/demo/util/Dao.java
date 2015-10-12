package demo.util;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * dbutils常用模板，使用log4jdbc监控sql执行，需要用到dao的地方直接注入即可
 * 
 * @author dj
 * 
 */
@Repository
public class Dao {

	// 主库数据源 master
	@Resource
	private DataSource dataSource;
	private QueryRunner queryRunner;



	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 *            sql语句
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int update(String sql) {
		return update(sql, null);
	}

	public int updateByTranscation(String sql, Connection conn,
			Object... params) {
		queryRunner = new QueryRunner();
		int affectedRows = 0;
		try {
			if (params == null) {
				affectedRows = queryRunner.update(conn, sql);
			} else {
				affectedRows = queryRunner.update(conn, sql, params);
			}
		} catch (SQLException e) {

		}
		return affectedRows;
	}

	/**
	 * 执行sql语句 <code> 
	 * executeUpdate("update user set username = 'kitty' where username = ?", "hello kitty"); 
	 * </code>
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int update(String sql, Object param) {
		return update(sql, new Object[] { param });
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 受影响的行数
	 * @throws SQLException
	 */
	public int update(String sql, Object[] params) {
		queryRunner = new QueryRunner(dataSource);
		int affectedRows = 0;
		try {
			if (params == null) {
				affectedRows = queryRunner.update(sql);
			} else {
				affectedRows = queryRunner.update(sql, params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return affectedRows;
	}

	/**
	 * 执行批量sql语句
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            二维参数数组
	 * @return 受影响的行数的数组
	 * @throws SQLException
	 */
	public int[] batchUpdate(String sql, Object[][] params) {
		queryRunner = new QueryRunner(dataSource);
		int[] affectedRows = new int[0];
		try {
			affectedRows = queryRunner.batch(sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return affectedRows;
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @return 查询结果
	 * @throws SQLException
	 */
	public List<Map<String, Object>> find(String sql) {
		return find(sql, null);
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 查询结果
	 * @throws SQLException
	 */
	public List<Map<String, Object>> find(String sql, Object param) {
		return find(sql, new Object[] { param });
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 * @throws SQLException
	 */
	public List<Map<String, Object>> find(String sql, Object[] params) {
			queryRunner = new QueryRunner(dataSource);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			if (params == null) {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler());
			} else {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Map<String, Object>> find(String sql, Object[] params,
			boolean useMaster) {
			queryRunner = new QueryRunner(dataSource);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			if (params == null) {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler());
			} else {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 求count
	// TODO 子查询等。。进行优化
	public long count(String sql, Object[] params) {
		// count(Type)返回的是Long类型,无奈
		Long retVal = 0L;
		try {
			sql = "select count(*) as count "
					+ sql.substring(sql.indexOf("from"));
			Map<String, Object> first = findFirst(sql, params);
			retVal = Long.valueOf(first.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * 执行查询，将每行的结果保存到一个Map对象中，然后将所有Map对象保存到List中,然后在拼装成page对象
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 * @throws Exception
	 */
	public Page<Map<String, Object>> find(String sql, Object[] params,
			PageRequest pageRequest) {
			queryRunner = new QueryRunner(dataSource);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Long count = count(sql, params);
		String limit = " limit " + pageRequest.getOffset() + " ,"
				+ pageRequest.getPageSize();
		sql += limit;

		try {
			if (params == null) {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler());
			} else {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new PageImpl<Map<String, Object>>(list, pageRequest, count);
	}

	public Page<Map<String, Object>> find(String sql, Object[] params,
			PageRequest pageRequest, boolean useMaster) throws Exception {

			queryRunner = new QueryRunner(dataSource);


		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Long count = count(sql, params);
		String limit = " limit " + pageRequest.getOffset() + " ,"
				+ pageRequest.getPageSize();
		sql += limit;

		try {
			if (params == null) {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler());
			} else {
				list = (List<Map<String, Object>>) queryRunner.query(sql,
						new MapListHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new PageImpl<Map<String, Object>>(list, pageRequest, count);
	}

	// 求count
	// TODO 子查询等。。进行优化
	public long count1(String sql, Object[] params) throws Exception {
		// count(Type)返回的是Long类型,无奈
		Long retVal = 0L;
		try {
			sql = "select count(*) as count from (" + sql + ") t";
			Map<String, Object> first = findFirst(sql, params);
			retVal = Long.valueOf(first.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}



	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @return 查询结果
	 * @throws SQLException
	 */
	public <T> List<T> find(Class<T> entityClass, String sql) throws SQLException {
		return find(entityClass, sql, null);
	}

	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 查询结果
	 * @throws SQLException
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, Object param) throws SQLException {
		return find(entityClass, sql, new Object[] { param });
	}

	/**
	 * 执行查询，将每行的结果保存到Bean中，然后将所有Bean保存到List中
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 查询结果
	 * @throws SQLException
	 */
	public <T> List<T> find(Class<T> entityClass, String sql, Object[] params) throws SQLException {

			queryRunner = new QueryRunner(dataSource);

		List<T> list = new ArrayList<T>();
		try {
			if (params == null) {
				list = (List<T>) queryRunner.query(sql,
						getBeanList(entityClass));
			} else {
				list = (List<T>) queryRunner.query(sql,
						getBeanList(entityClass), params);
			}
		} catch (SQLException e) {
			//log.error(e.getMessage(), e);
			throw e;
		}
		return list;
	}

	public <T> List<T> find(Class<T> entityClass, String sql, Object[] params,
			boolean useMaster) {
		queryRunner = new QueryRunner(dataSource);

		List<T> list = new ArrayList<T>();
		try {
			if (params == null) {
				list = (List<T>) queryRunner.query(sql,
						getBeanList(entityClass));
			} else {
				list = (List<T>) queryRunner.query(sql,
						getBeanList(entityClass), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @return 对象
	 * @throws SQLException
	 */
	public <T> T findFirst(Class<T> entityClass, String sql) {
		return findFirst(entityClass, sql, null);
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 对象
	 * @throws SQLException
	 */
	public <T> T findFirst(Class<T> entityClass, String sql, Object param) {
		return findFirst(entityClass, sql, new Object[] { param });
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成对象
	 * 
	 * @param entityClass
	 *            类名
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 对象
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public <T> T findFirst(Class<T> entityClass, String sql, Object[] params) {
			queryRunner = new QueryRunner(dataSource);

		Object object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, getBean(entityClass));
			} else {
				object = queryRunner.query(sql, getBean(entityClass), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (T) object;
	}

	public <T> T findFirst(Class<T> entityClass, String sql, Object[] params,
			boolean useMaster) {
			queryRunner = new QueryRunner(dataSource);

		Object object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, getBean(entityClass));
			} else {
				object = queryRunner.query(sql, getBean(entityClass), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (T) object;
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @return 封装为Map的对象
	 * @throws SQLException
	 */
	public Map<String, Object> findFirst(String sql) {
		return findFirst(sql, null);
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @param param
	 *            参数
	 * @return 封装为Map的对象
	 * @throws SQLException
	 */
	public Map<String, Object> findFirst(String sql, Object param) {
		return findFirst(sql, new Object[] { param });
	}

	public Map<String, Object> findFirst(String sql, Object param,
			boolean useMaster) {
		return findFirst(sql, new Object[] { param }, useMaster);
	}

	/**
	 * 查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数数组
	 * @return 封装为Map的对象
	 * @throws SQLException
	 */
	public Map<String, Object> findFirst(String sql, Object[] params) {

			queryRunner = new QueryRunner(dataSource);

		Map<String, Object> map = null;
		try {
			if (params == null) {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler());
			} else {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 从有“写入”角色的数据源中，查询出结果集中的第一条记录，并封装成Map对象
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @author zengja
	 * @date 2014年8月12日 上午11:47:07
	 */
	public Map<String, Object> findFirstByWrite(String sql, Object... params) {
		queryRunner = new QueryRunner(dataSource);
		Map<String, Object> map = null;
		try {
			if (params == null) {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler());
			} else {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler(), params);
			}
		} catch (SQLException e) {
		e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> findFirst(String sql, Object[] params,
			boolean useMaster) {

			queryRunner = new QueryRunner(dataSource);

		Map<String, Object> map = null;
		try {
			if (params == null) {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler());
			} else {
				map = (Map<String, Object>) queryRunner.query(sql,
						new MapHandler(), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @return 结果对象
	 * @throws SQLException
	 */
	public Object findBy(String sql, String columnName) {
		return findBy(sql, columnName, null);
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @param param
	 *            参数
	 * @return 结果对象
	 * @throws SQLException
	 */
	public Object findBy(String sql, String columnName, Object param) {
		return findBy(sql, columnName, new Object[] { param });
	}

	/**
	 * 使用ScalarHandler处理单行记录，只返回结果集第一行中的指定字段，如未指定字段，则返回第一个字段！
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnName
	 *            列名
	 * @param params
	 *            参数数组
	 * @return 结果对象
	 * @throws SQLException
	 */
	public <T> T findBy(String sql, String columnName, Object[] params) {
			queryRunner = new QueryRunner(dataSource);

		T object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql,
						new ScalarHandler<T>(columnName));
			} else {
				object = queryRunner.query(sql,
						new ScalarHandler<T>(columnName), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		}
		return object;
	}

	public <T> T findBy(String sql, String columnName, Object[] params,
			boolean useMaster) {
			queryRunner = new QueryRunner(dataSource);

		T object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql,
						new ScalarHandler<T>(columnName));
			} else {
				object = queryRunner.query(sql,
						new ScalarHandler<T>(columnName), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @return 结果对象
	 * @throws SQLException
	 */
	public Object findBy(String sql, int columnIndex) {
		return findBy(sql, columnIndex, null);
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @param param
	 *            参数
	 * @return 结果对象
	 * @throws SQLException
	 */
	public Object findBy(String sql, int columnIndex, Object param) {
		return findBy(sql, columnIndex, new Object[] { param });
	}

	/**
	 * 查询某一条记录，并将指定列的数据转换为Object
	 * 
	 * @param sql
	 *            sql语句
	 * @param columnIndex
	 *            列索引
	 * @param params
	 *            参数数组
	 * @return 结果对象
	 * @throws SQLException
	 */
	public <T> T findBy(String sql, int columnIndex, Object[] params) {

			queryRunner = new QueryRunner(dataSource);

		T object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, new ScalarHandler<T>(
						columnIndex));
			} else {
				object = queryRunner.query(sql, new ScalarHandler<T>(
						columnIndex), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return object;
	}

	public <T> T findBy(String sql, int columnIndex, Object[] params,
			boolean useMaster) {

			queryRunner = new QueryRunner(dataSource);

		T object = null;
		try {
			if (params == null) {
				object = queryRunner.query(sql, new ScalarHandler<T>(
						columnIndex));
			} else {
				object = queryRunner.query(sql, new ScalarHandler<T>(
						columnIndex), params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return object;
	}

	public <T> BeanListHandler<T> getBeanList(Class<T> clazz) {
		return new BeanListHandler<T>(clazz, new BasicRowProcessor(
				new HumpBeanProcessor(new HumpMatcher())));
	}

	public <T> BeanHandler<T> getBean(Class<T> clazz) {
		return new BeanHandler<T>(clazz, new BasicRowProcessor(
				new HumpBeanProcessor(new HumpMatcher())));
	}

	/**
	 * 获取数据库连接
	 * 
	 * @return 数据库连接 连接方式
	 */
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	/**
	 * @param sql
	 * @param paramList
	 * @return
	 * @throws SQLException
	 */
	public Long updateGetID(String sql, List<Object> paramList)
			throws SQLException {
		PreparedStatement ps = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < paramList.size(); i++) {
				ps.setObject(i + 1, paramList.get(i));
			}
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			return rs.next() ? rs.getLong(1) : -1;
		} catch (SQLException e) {
			throw e;
		} finally {
			//update:ym 20140523  关闭connection
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(ps);
			sql = null;
		}
	}

	public int updateByTranscation(String sql, Object[] params, Connection conn)
			throws SQLException {
		queryRunner = new QueryRunner();
		int affectedRows = 0;
		try {
			if (params == null) {
				affectedRows = queryRunner.update(conn, sql);
			} else {
				affectedRows = queryRunner.update(conn, sql, params);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;

		}
		return affectedRows;
	}
}