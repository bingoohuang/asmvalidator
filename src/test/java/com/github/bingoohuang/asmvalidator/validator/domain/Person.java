package com.github.bingoohuang.asmvalidator.validator.domain;


import com.github.bingoohuang.asmvalidator.annotations.AsmIgnore;
import lombok.*;

@Data @NoArgsConstructor @ToString
public class Person {
    @Getter @Setter String name;
    @Getter @Setter String addr;

    @AsmIgnore String code;

    public Person(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (name != null ? !name.equals(person.name) : person.name != null)
            return false;
        return !(addr != null ? !addr.equals(person.addr) : person.addr != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (addr != null ? addr.hashCode() : 0);
        return result;
    }
}
