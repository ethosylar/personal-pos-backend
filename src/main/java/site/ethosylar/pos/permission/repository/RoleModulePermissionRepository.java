package site.ethosylar.pos.permission.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.ethosylar.pos.permission.entity.RoleModulePermissionEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleModulePermissionRepository
        extends JpaRepository<RoleModulePermissionEntity, Long> {

    List<RoleModulePermissionEntity>
        findByRoleModule_Id(Long roleModuleId);

    boolean existsByRoleModule_IdAndModulePermission_Id(
        Long roleModuleId,
        Long modulePermissionId
    );
    
    @Query(
    	    value = """
    	        SELECT EXISTS (
    	            SELECT 1
    	            FROM dt_users_usr usr

    	            INNER JOIN st_user_status_us us
    	                ON us.id = usr.user_status_id

    	            INNER JOIN dt_user_roles_ur ur
    	                ON ur.user_id = usr.id

    	            INNER JOIN lt_roles_rl rl
    	                ON rl.id = ur.role_id

    	            INNER JOIN dt_role_modules_rm rm
    	                ON rm.role_id = rl.id

    	            INNER JOIN lt_modules_mod mod
    	                ON mod.id = rm.module_id

    	            INNER JOIN dt_role_module_permissions_rmp rmp
    	                ON rmp.role_module_id = rm.id

    	            INNER JOIN lt_module_permissions_mp mp
    	                ON mp.id = rmp.module_permission_id

    	            WHERE LOWER(usr.username)
    	                    = LOWER(:username)

    	              AND us.code = 'ACTIVE'

    	              AND rl.is_active = TRUE
    	              AND mod.is_active = TRUE
    	              AND mp.is_active = TRUE

    	              AND mod.code = :moduleCode
    	              AND mp.code = :permissionCode
    	        )
    	        """,
    	    nativeQuery = true
    	)
    	boolean existsEffectivePermission(
    	        @Param("username") String username,
    	        @Param("moduleCode") String moduleCode,
    	        @Param("permissionCode") String permissionCode
    	);


    	@Query(
    	    value = """
    	        SELECT DISTINCT
    	            mod.code || ':' || mp.code
    	        FROM dt_users_usr usr

    	        INNER JOIN st_user_status_us us
    	            ON us.id = usr.user_status_id

    	        INNER JOIN dt_user_roles_ur ur
    	            ON ur.user_id = usr.id

    	        INNER JOIN lt_roles_rl rl
    	            ON rl.id = ur.role_id

    	        INNER JOIN dt_role_modules_rm rm
    	            ON rm.role_id = rl.id

    	        INNER JOIN lt_modules_mod mod
    	            ON mod.id = rm.module_id

    	        INNER JOIN dt_role_module_permissions_rmp rmp
    	            ON rmp.role_module_id = rm.id

    	        INNER JOIN lt_module_permissions_mp mp
    	            ON mp.id = rmp.module_permission_id

    	        WHERE LOWER(usr.username)
    	                = LOWER(:username)

    	          AND us.code = 'ACTIVE'

    	          AND rl.is_active = TRUE
    	          AND mod.is_active = TRUE
    	          AND mp.is_active = TRUE

    	        ORDER BY 1
    	        """,
    	    nativeQuery = true
    	)
    	List<String> findEffectivePermissions(
    	        @Param("username") String username
    	);
}