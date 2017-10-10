package com.xyz.pw.api.data.model.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/**
 * The persistent class for the unit database table.
 * 
 */
@Entity
@Table(name = "unit")
@GenericGenerator(name = "shard_gen", //
    strategy = "com.xyz.pw.api.data.model.id.ShardIdGenerator", //
    parameters = {@Parameter(name = "segment_value", value = "unit"), //
        @Parameter(name = "table_name", //
            value = "shard_hibernate_sequence"), //
        @Parameter(name = "value_column_name", //
            value = "sequence_next_hi_value")})
public class Unit implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "shard_gen")
  @Column(name = "id")
  private Long id;

  @Column(name = "abbreviation")
  private String abbreviation;
 

}
