package at.ac.fhsalzburg.swd.spring.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter // I am lazy and using project lombok which provides annotations to create getter/setter automatically for private members
@Setter // this is nice, less code to write but a bit of brute-force and not so fine-grained than manually.
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany
    @ElementCollection
    private Set<Chair> chairs = new HashSet<>();

    public Restaurant() {
    }

    public Restaurant(String name) {
        this.name = name;
    }
}
