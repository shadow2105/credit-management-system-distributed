package com.example.cmstransactionservice.domain.repository;

import com.example.cmstransactionservice.domain.CreditAccount_;
import com.example.cmstransactionservice.domain.Transaction;
import com.example.cmstransactionservice.domain.Transaction_;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


// https://stackoverflow.com/questions/14014086/what-is-difference-between-crudrepository-and-jparepository-interfaces-in-spring
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    /*
     * view all transactions by customerId ordered by transactionDateTime
     *
      SELECT t.transaction_id, t.transaction_datetime, t.post_datetime, t.description, t.type, t.amount,
      t.credit_account_id, c.customer_id
      FROM transactions t
      JOIN credit_accounts c
      ON t.credit_account_id = c.id
      WHERE c.customer_id = ?
      ORDER BY t.transaction_datetime;

     * We didn't explicitly specify the join column because Spring Data JPA automatically infers the join column based
     * on the relationship between entities. In the Transaction entity, the @ManyToOne annotation is used to define the
     * relationship with the CreditAccount entity. By default, Spring Data JPA will look for a foreign key column
     * (@JoinColumn) in the Transaction table that references a column of the CreditAccount table (id).
     * It infers this based on the naming convention and the association between entities.
     */

    // filter these transactions by credit statement id (or account number), transaction month, year and type

    @Query("SELECT t FROM Transaction t JOIN t.creditAccount c WHERE c.customerId = ?1 ORDER BY t.transactionDateTime")
    Slice<Transaction> findAllByCustomerIdOrderByTransactionDateTime(String customerId, Pageable pageable);

    //

    /*
     * view all transactions by customerId and credit account's id ordered by transactionDateTime
     *
      SELECT t.transaction_id, t.transaction_datetime, t.post_datetime, t.description, t.type, t.amount,
      t.credit_account_id, c.customer_id
      FROM transactions t
      JOIN credit_accounts c
      ON t.credit_account_id = c.id
      WHERE c.customer_id = ? AND c.id = ?
      ORDER BY t.transaction_datetime;
     *
     */
    @Query("SELECT t FROM Transaction t JOIN t.creditAccount c WHERE c.customerId = ?1 AND c.id = ?2 ORDER BY t.transactionDateTime")
    Slice<Transaction> findAllByCustomerIdAndCreditAccount_IdOrderByTransactionDateTime(String customerId,
                                                                                       UUID creditAccountId,
                                                                                       Pageable pageable);

    // save a new transaction
    // Transaction save(Transaction transaction);

    interface Specs {

        static Specification<Transaction> byCustomerId(String customerId) {
            return (root, query, builder) ->
                    builder.equal(root.get(Transaction_.creditAccount)
                            .get(CreditAccount_.customerId), customerId);
        }

        static Specification<Transaction> byCreditAccountId(String creditAccountId) {
            return (root, query, builder) ->
                    builder.equal(root.get(Transaction_.creditAccount)
                            .get(CreditAccount_.id), UUID.fromString(creditAccountId));
        }

        static Specification<Transaction> byAccountNumber(String accountNumber) {
            return (root, query, builder) ->
                    builder.equal(root.get(Transaction_.creditAccount)
                            .get(CreditAccount_.accountNumber), accountNumber);
        }

        static Specification<Transaction> byType(char type) {
            return (root, query, builder) ->
                    builder.equal(root.get(Transaction_.type), type);
        }

        static Specification<Transaction> byMonthAndYear(String month, String year) {
            return (root, query, builder) ->
                    builder.and(
                        builder.equal(
                                builder.function(
                                        "YEAR",
                                        Integer.class,
                                        root.get(Transaction_.transactionDateTime)
                                ),
                                Integer.parseInt(year)
                        ),
                        builder.equal(
                                builder.function(
                                        "MONTH",
                                        Integer.class,
                                        root.get(Transaction_.transactionDateTime)
                                ),
                                Integer.parseInt(month)
                        )
                    );
        }

        static Specification<Transaction> orderByTransactionDateTime(Specification<Transaction> spec) {
            return (root, query, builder) -> {
                query.orderBy(
                        builder.asc(root.get(Transaction_.transactionDateTime))
                );

                return spec.toPredicate(root, query, builder);
            };
        }
    }
}

/*
* Additional Notes -
*
* CrudRepository is the basic interface for CRUD operations in Spring Data. It provides generic CRUD methods like save,
* findById, findAll, delete, etc. t's a generic interface, so it can work with any entity type. It provides basic CRUD
* operations without any specific query methods.
*
*
* PagingAndSortingRepository extends CrudRepository. In addition to CRUD operations, it provides methods for pagination
* and sorting of query results. This is useful when dealing with large datasets, and you want to fetch data in smaller
* chunks (pages).
*
*
* JpaRepository extends PagingAndSortingRepository. It adds JPA-specific methods ( like flush, saveAndFlush,
* deleteInBatch, findAll(Specification)) on top of the basic CRUD and pagination/sorting functionality provided by its
* parent interfaces.
*
*   - flush: The flush() method is used to synchronize the changes made in the persistence context with the underlying
* database. When you're working with JPA, the changes made to entities are often held in the persistence context until
* certain synchronization points are reached. The flush() method forces these changes to be written to the database
* immediately. This can be useful in scenarios where you need to ensure that changes are persisted before executing a
* specific operation or when you need to guarantee the consistency of data in the database.
*
*   - saveAndFlush: The saveAndFlush() method is a combination of saving an entity and immediately flushing changes to the
* database in a single operation.
*
*   - deleteInBatch: The deleteInBatch(Iterable<T> entities) method deletes a batch of entities in a single database
* operation. Instead of deleting entities one by one, which can result in multiple database calls, deleteInBatch()
* groups the delete operations into a single batch operation, typically resulting in better performance.
*
*   - findAll(Specification): The findAll(Specification) method retrieves entities from the database based on a given
* Specification (a predicate-based abstraction for building dynamic queries). This method is useful when you need to
* retrieve entities based on varying conditions that are determined at runtime, such as filtering based on user input
* or dynamic search criteria.
*/

/*
* The JPA Metamodel provides a way to access metadata about managed entity classes in a type-safe manner. When we write
* criteria queries in Hibernate (or any other JPA provider), we often need to reference entity classes and their
* attributes. Traditionally, we would use attribute names as strings, but this approach has several downsides:
*   - We have to look up the names of entity attributes.
*   - If a column name changes during the project lifecycle, we need to refactor all queries using that name.
*
* The JPA Metamodel was introduced to avoid these drawbacks by providing static access to the metadata of managed
* entity classes. To use the JPA Metamodel, we first need to generate the metamodel classes.
* Tools like JBoss, EclipseLink, OpenJPA, and DataNucleus can generate these classes.
*
* A generated metamodel class resides in the same package as the corresponding entity class. It has the same name as
* the entity class with an added underscore (_) at the end.
* A Metamodel class is automatically generated during the build process (using tools like Hibernate’s hibernate-jpamodelgen)
*
* We can use the static metamodel classes just like we would use string references to attributes. The criteria query
* API provides overloaded methods that accept both string references and Attribute interface implementations.
*
* The underscore convention in method names like findAllByCustomerIdAndCreditAccount_IdOrderByTransactionDateTime is
* not directly related to JPA metamodel. It is just part of the naming convention used in Spring Data JPA repositories
* to delineate properties and their nested properties, aiding in the generation of appropriate queries.
*/

/*
* In the context of Domain-Driven Design (DDD), a "Specification" is a pattern used to encapsulate a business rule or a
* query criterion (within a well-defined unit or class). It provides a way to express criteria that are used to select
* objects from a domain model. Specifications are typically used for querying entities based on specific conditions or
* for checking whether an entity satisfies certain criteria. By encapsulating these criteria, they provide a clear and
* reusable way to express complex business rules or query conditions within the domain model.
*
* Here's how Specifications are commonly implemented in DDD:
*   - Interface: Typically, a Specification is defined as an interface. This interface declares a method that specifies
* whether an object satisfies the criteria defined by the Specification.
*
*   - Implementation: Concrete classes are created to implement the Specification interface. These classes encapsulate
* the logic to evaluate whether an object meets the specified criteria.
*
*   - Usage: Specifications are then used in various parts of the application, such as repositories or services, to
* query for objects or to check whether objects satisfy certain conditions.
*
* By leveraging the JPA Criteria API, developers can implement Specifications in a modular, reusable, and type-safe
* manner, thus achieving a high degree of expressiveness and flexibility in query construction. This approach also
* helps in separating concerns by keeping domain logic (Specifications) separate from infrastructure concerns
* (query construction).
*/


/*
* The JPA Criteria API is a programmatic and type-safe way to build queries for entities in Java Persistence. It allows
* developers to construct queries dynamically at runtime using Java code, rather than relying on JPQL (Java Persistence
* Query Language) strings, which are prone to syntax errors and lack compile-time type checking.
*
* Key features of the JPA Criteria API include:
*   - Type Safety: The Criteria API provides type-safe constructs for building queries. This means that queries are
* checked for correctness at compile time, reducing the likelihood of runtime errors.
*
*   - Programmatic Query Building: Queries are constructed using method calls and fluent interfaces, enabling dynamic
* query generation based on runtime conditions.
*
*   - Static Metamodel: The Criteria API often leverages static metamodels generated by JPA providers, which provide
* compile-time type information about entity attributes. This allows for type-safe attribute access and prevents errors
* caused by typos in attribute names.
*   - Support for Complex Queries: The Criteria API supports complex query constructs such as joins, subqueries,
* predicates, and aggregate functions, enabling the construction of sophisticated queries.
*
* Component of the JPA Criteria API:
*
*   - CriteriaBuilder: CriteriaBuilder is a factory for creating various criteria-related objects such as CriteriaQuery,
* Predicate, and Order. It provides methods for constructing different parts of a query, including selecting entities,
* defining conditions (predicates), grouping, ordering, and aggregating results (such as equal, like, greaterThan, etc.).
* CriteriaBuilder is obtained from an EntityManager instance using the getCriteriaBuilder() method.
*
*   - CriteriaQuery: CriteriaQuery represents the overall structure of a query. It defines what data will be retrieved
* from the database. It can be used to specify the root entity, selection criteria, grouping, ordering, and other query
* details. Once constructed, a CriteriaQuery object can be executed to retrieve results from the database.
*
*   - Root: Root represents the root entity in a query . It is used to specify the entity type being queried and serves
* as the starting point for navigating to other related entities using joins. Root instances are typically obtained from
* a CriteriaQuery using the from() method, passing the entity class as an argument.
*
*   - Predicate: Predicate represents a condition or criterion in a query. It defines the filtering criteria used to
* select entities that meet certain conditions. Predicates can be simple comparisons (e.g., equality, greater than,
* less than) or complex conditions formed by combining multiple predicates with logical operators (AND, OR, NOT).
* Predicates are typically constructed using methods provided by the CriteriaBuilder class, such as equal(),
* greaterThan(), like(), etc.
*/