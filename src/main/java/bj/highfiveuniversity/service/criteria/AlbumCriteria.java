package bj.highfiveuniversity.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link bj.highfiveuniversity.domain.Album} entity. This class is used
 * in {@link bj.highfiveuniversity.web.rest.AlbumResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /albums?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AlbumCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter ref;

    private StringFilter author;

    private StringFilter title;

    private StringFilter description;

    private IntegerFilter duration;

    private Boolean distinct;

    public AlbumCriteria() {}

    public AlbumCriteria(AlbumCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.ref = other.optionalRef().map(StringFilter::copy).orElse(null);
        this.author = other.optionalAuthor().map(StringFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.duration = other.optionalDuration().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AlbumCriteria copy() {
        return new AlbumCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getRef() {
        return ref;
    }

    public Optional<StringFilter> optionalRef() {
        return Optional.ofNullable(ref);
    }

    public StringFilter ref() {
        if (ref == null) {
            setRef(new StringFilter());
        }
        return ref;
    }

    public void setRef(StringFilter ref) {
        this.ref = ref;
    }

    public StringFilter getAuthor() {
        return author;
    }

    public Optional<StringFilter> optionalAuthor() {
        return Optional.ofNullable(author);
    }

    public StringFilter author() {
        if (author == null) {
            setAuthor(new StringFilter());
        }
        return author;
    }

    public void setAuthor(StringFilter author) {
        this.author = author;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public IntegerFilter getDuration() {
        return duration;
    }

    public Optional<IntegerFilter> optionalDuration() {
        return Optional.ofNullable(duration);
    }

    public IntegerFilter duration() {
        if (duration == null) {
            setDuration(new IntegerFilter());
        }
        return duration;
    }

    public void setDuration(IntegerFilter duration) {
        this.duration = duration;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AlbumCriteria that = (AlbumCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ref, that.ref) &&
            Objects.equals(author, that.author) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ref, author, title, description, duration, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AlbumCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRef().map(f -> "ref=" + f + ", ").orElse("") +
            optionalAuthor().map(f -> "author=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalDuration().map(f -> "duration=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
