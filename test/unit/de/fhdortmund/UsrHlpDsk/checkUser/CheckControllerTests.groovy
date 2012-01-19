package de.fhdortmund.UsrHlpDsk.checkUser



import org.junit.*
import grails.test.mixin.*

@TestFor(CheckController)
@Mock(Check)
class CheckControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/check/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.checkInstanceList.size() == 0
        assert model.checkInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.checkInstance != null
    }

    void testSave() {
        controller.save()

        assert model.checkInstance != null
        assert view == '/check/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/check/show/1'
        assert controller.flash.message != null
        assert Check.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/check/list'


        populateValidParams(params)
        def check = new Check(params)

        assert check.save() != null

        params.id = check.id

        def model = controller.show()

        assert model.checkInstance == check
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/check/list'


        populateValidParams(params)
        def check = new Check(params)

        assert check.save() != null

        params.id = check.id

        def model = controller.edit()

        assert model.checkInstance == check
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/check/list'

        response.reset()


        populateValidParams(params)
        def check = new Check(params)

        assert check.save() != null

        // test invalid parameters in update
        params.id = check.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/check/edit"
        assert model.checkInstance != null

        check.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/check/show/$check.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        check.clearErrors()

        populateValidParams(params)
        params.id = check.id
        params.version = -1
        controller.update()

        assert view == "/check/edit"
        assert model.checkInstance != null
        assert model.checkInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/check/list'

        response.reset()

        populateValidParams(params)
        def check = new Check(params)

        assert check.save() != null
        assert Check.count() == 1

        params.id = check.id

        controller.delete()

        assert Check.count() == 0
        assert Check.get(check.id) == null
        assert response.redirectedUrl == '/check/list'
    }
}
