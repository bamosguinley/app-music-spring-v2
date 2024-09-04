package bj.highfiveuniversity.web.rest;

import static bj.highfiveuniversity.domain.AlbumAsserts.*;
import static bj.highfiveuniversity.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bj.highfiveuniversity.IntegrationTest;
import bj.highfiveuniversity.domain.Album;
import bj.highfiveuniversity.repository.AlbumRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AlbumResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AlbumResourceIT {

    private static final String DEFAULT_REF = "AAAAAAAAAA";
    private static final String UPDATED_REF = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_DURATION = 1;
    private static final Integer UPDATED_DURATION = 2;
    private static final Integer SMALLER_DURATION = 1 - 1;

    private static final String ENTITY_API_URL = "/api/albums";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAlbumMockMvc;

    private Album album;

    private Album insertedAlbum;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createEntity() {
        return new Album()
            .ref(DEFAULT_REF)
            .author(DEFAULT_AUTHOR)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .duration(DEFAULT_DURATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Album createUpdatedEntity() {
        return new Album()
            .ref(UPDATED_REF)
            .author(UPDATED_AUTHOR)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION);
    }

    @BeforeEach
    public void initTest() {
        album = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAlbum != null) {
            albumRepository.delete(insertedAlbum);
            insertedAlbum = null;
        }
    }

    @Test
    @Transactional
    void createAlbum() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Album
        var returnedAlbum = om.readValue(
            restAlbumMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(album)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Album.class
        );

        // Validate the Album in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAlbumUpdatableFieldsEquals(returnedAlbum, getPersistedAlbum(returnedAlbum));

        insertedAlbum = returnedAlbum;
    }

    @Test
    @Transactional
    void createAlbumWithExistingId() throws Exception {
        // Create the Album with an existing ID
        album.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAlbumMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(album)))
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAlbums() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(album.getId().intValue())))
            .andExpect(jsonPath("$.[*].ref").value(hasItem(DEFAULT_REF)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)));
    }

    @Test
    @Transactional
    void getAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get the album
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL_ID, album.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(album.getId().intValue()))
            .andExpect(jsonPath("$.ref").value(DEFAULT_REF))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION));
    }

    @Test
    @Transactional
    void getAlbumsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        Long id = album.getId();

        defaultAlbumFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAlbumFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAlbumFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAlbumsByRefIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where ref equals to
        defaultAlbumFiltering("ref.equals=" + DEFAULT_REF, "ref.equals=" + UPDATED_REF);
    }

    @Test
    @Transactional
    void getAllAlbumsByRefIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where ref in
        defaultAlbumFiltering("ref.in=" + DEFAULT_REF + "," + UPDATED_REF, "ref.in=" + UPDATED_REF);
    }

    @Test
    @Transactional
    void getAllAlbumsByRefIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where ref is not null
        defaultAlbumFiltering("ref.specified=true", "ref.specified=false");
    }

    @Test
    @Transactional
    void getAllAlbumsByRefContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where ref contains
        defaultAlbumFiltering("ref.contains=" + DEFAULT_REF, "ref.contains=" + UPDATED_REF);
    }

    @Test
    @Transactional
    void getAllAlbumsByRefNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where ref does not contain
        defaultAlbumFiltering("ref.doesNotContain=" + UPDATED_REF, "ref.doesNotContain=" + DEFAULT_REF);
    }

    @Test
    @Transactional
    void getAllAlbumsByAuthorIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where author equals to
        defaultAlbumFiltering("author.equals=" + DEFAULT_AUTHOR, "author.equals=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllAlbumsByAuthorIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where author in
        defaultAlbumFiltering("author.in=" + DEFAULT_AUTHOR + "," + UPDATED_AUTHOR, "author.in=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllAlbumsByAuthorIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where author is not null
        defaultAlbumFiltering("author.specified=true", "author.specified=false");
    }

    @Test
    @Transactional
    void getAllAlbumsByAuthorContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where author contains
        defaultAlbumFiltering("author.contains=" + DEFAULT_AUTHOR, "author.contains=" + UPDATED_AUTHOR);
    }

    @Test
    @Transactional
    void getAllAlbumsByAuthorNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where author does not contain
        defaultAlbumFiltering("author.doesNotContain=" + UPDATED_AUTHOR, "author.doesNotContain=" + DEFAULT_AUTHOR);
    }

    @Test
    @Transactional
    void getAllAlbumsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where title equals to
        defaultAlbumFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAlbumsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where title in
        defaultAlbumFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAlbumsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where title is not null
        defaultAlbumFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllAlbumsByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where title contains
        defaultAlbumFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllAlbumsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where title does not contain
        defaultAlbumFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllAlbumsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where description equals to
        defaultAlbumFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where description in
        defaultAlbumFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where description is not null
        defaultAlbumFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllAlbumsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where description contains
        defaultAlbumFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where description does not contain
        defaultAlbumFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration equals to
        defaultAlbumFiltering("duration.equals=" + DEFAULT_DURATION, "duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration in
        defaultAlbumFiltering("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION, "duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration is not null
        defaultAlbumFiltering("duration.specified=true", "duration.specified=false");
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration is greater than or equal to
        defaultAlbumFiltering("duration.greaterThanOrEqual=" + DEFAULT_DURATION, "duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration is less than or equal to
        defaultAlbumFiltering("duration.lessThanOrEqual=" + DEFAULT_DURATION, "duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration is less than
        defaultAlbumFiltering("duration.lessThan=" + UPDATED_DURATION, "duration.lessThan=" + DEFAULT_DURATION);
    }

    @Test
    @Transactional
    void getAllAlbumsByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        // Get all the albumList where duration is greater than
        defaultAlbumFiltering("duration.greaterThan=" + SMALLER_DURATION, "duration.greaterThan=" + DEFAULT_DURATION);
    }

    private void defaultAlbumFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAlbumShouldBeFound(shouldBeFound);
        defaultAlbumShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAlbumShouldBeFound(String filter) throws Exception {
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(album.getId().intValue())))
            .andExpect(jsonPath("$.[*].ref").value(hasItem(DEFAULT_REF)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION)));

        // Check, that the count call also returns 1
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAlbumShouldNotBeFound(String filter) throws Exception {
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAlbumMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAlbum() throws Exception {
        // Get the album
        restAlbumMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album
        Album updatedAlbum = albumRepository.findById(album.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAlbum are not directly saved in db
        em.detach(updatedAlbum);
        updatedAlbum
            .ref(UPDATED_REF)
            .author(UPDATED_AUTHOR)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION);

        restAlbumMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAlbum.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAlbum))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAlbumToMatchAllProperties(updatedAlbum);
    }

    @Test
    @Transactional
    void putNonExistingAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(put(ENTITY_API_URL_ID, album.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(album)))
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(album))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(album)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum.ref(UPDATED_REF).author(UPDATED_AUTHOR).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlbum))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlbumUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAlbum, album), getPersistedAlbum(album));
    }

    @Test
    @Transactional
    void fullUpdateAlbumWithPatch() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the album using partial update
        Album partialUpdatedAlbum = new Album();
        partialUpdatedAlbum.setId(album.getId());

        partialUpdatedAlbum
            .ref(UPDATED_REF)
            .author(UPDATED_AUTHOR)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .duration(UPDATED_DURATION);

        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAlbum.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAlbum))
            )
            .andExpect(status().isOk());

        // Validate the Album in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAlbumUpdatableFieldsEquals(partialUpdatedAlbum, getPersistedAlbum(partialUpdatedAlbum));
    }

    @Test
    @Transactional
    void patchNonExistingAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, album.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(album))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(album))
            )
            .andExpect(status().isBadRequest());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAlbum() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        album.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAlbumMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(album)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Album in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAlbum() throws Exception {
        // Initialize the database
        insertedAlbum = albumRepository.saveAndFlush(album);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the album
        restAlbumMockMvc
            .perform(delete(ENTITY_API_URL_ID, album.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return albumRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Album getPersistedAlbum(Album album) {
        return albumRepository.findById(album.getId()).orElseThrow();
    }

    protected void assertPersistedAlbumToMatchAllProperties(Album expectedAlbum) {
        assertAlbumAllPropertiesEquals(expectedAlbum, getPersistedAlbum(expectedAlbum));
    }

    protected void assertPersistedAlbumToMatchUpdatableProperties(Album expectedAlbum) {
        assertAlbumAllUpdatablePropertiesEquals(expectedAlbum, getPersistedAlbum(expectedAlbum));
    }
}
