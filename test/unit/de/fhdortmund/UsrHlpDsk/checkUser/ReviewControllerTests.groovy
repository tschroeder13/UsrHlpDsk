package de.fhdortmund.UsrHlpDsk.checkUser



import org.junit.*
import grails.test.mixin.*

@TestFor(ReviewController)
@Mock(Review)
class ReviewControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/review/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.reviewInstanceList.size() == 0
        assert model.reviewInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.reviewInstance != null
    }

    void testSave() {
        controller.save()

        assert model.reviewInstance != null
        assert view == '/review/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/review/show/1'
        assert controller.flash.message != null
        assert Review.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/review/list'


        populateValidParams(params)
        def review = new Review(params)

        assert review.save() != null

        params.id = review.id

        def model = controller.show()

        assert model.reviewInstance == review
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/review/list'


        populateValidParams(params)
        def review = new Review(params)

        assert review.save() != null

        params.id = review.id

        def model = controller.edit()

        assert model.reviewInstance == review
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/review/list'

        response.reset()


        populateValidParams(params)
        def review = new Review(params)

        assert review.save() != null

        // test invalid parameters in update
        params.id = review.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/review/edit"
        assert model.reviewInstance != null

        review.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/review/show/$review.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        review.clearErrors()

        populateValidParams(params)
        params.id = review.id
        params.version = -1
        controller.update()

        assert view == "/review/edit"
        assert model.reviewInstance != null
        assert model.reviewInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/review/list'

        response.reset()

        populateValidParams(params)
        def review = new Review(params)

        assert review.save() != null
        assert Review.count() == 1

        params.id = review.id

        controller.delete()

        assert Review.count() == 0
        assert Review.get(review.id) == null
        assert response.redirectedUrl == '/review/list'
    }
}
