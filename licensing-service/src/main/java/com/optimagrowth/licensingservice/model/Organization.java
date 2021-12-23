package com.optimagrowth.licensingservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString

/*Sets the name of the hash in the Redis server where the organization data is stored*/
@RedisHash("organization")
public class Organization extends RepresentationModel<Organization> {

  String id;
  String name;
  String contactName;
  String contactEmail;
  String contactPhone;
}
