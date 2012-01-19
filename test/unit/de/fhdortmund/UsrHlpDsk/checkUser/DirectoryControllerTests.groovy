package de.fhdortmund.UsrHlpDsk.checkUser



import org.junit.*
import grails.test.mixin.*

@TestFor(DirectoryController)
@Mock(Directory)
class DirectoryControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/directory/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.directoryInstanceList.size() == 0
        assert model.directoryInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.directoryInstance != null
    }

    void testSave() {
        controller.save()

        assert model.directoryInstance != null
        assert view == '/directory/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/directory/show/1'
        assert controller.flash.message != null
        assert Directory.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/directory/list'


        populateValidParams(params)
        def directory = new Directory(params)

        assert directory.save() != null

        params.id = directory.id

        def model = controller.show()

        assert model.directoryInstance == directory
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/directory/list'


        populateValidParams(params)
        def directory = new Directory(params)

        assert directory.save() != null

        params.id = directory.id

        def model = controller.edit()

        assert model.directoryInstance == directory
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/directory/list'

        response.reset()


        populateValidParams(params)
        def directory = new Directory(params)

        assert directory.save() != null

        // test invalid parameters in update
        params.id = directory.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/directory/edit"
        assert model.directoryInstance != null

        directory.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/directory/show/$directory.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        directory.clearErrors()

        populateValidParams(params)
        params.id = directory.id
        params.version = -1
        controller.update()

        assert view == "/directory/edit"
        assert model.directoryInstance != null
        assert model.directoryInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/directory/list'

        response.reset()

        populateValidParams(params)
        def directory = new Directory(params)

        assert directory.save() != null
        assert Directory.count() == 1

        params.id = directory.id

        controller.delete()

        assert Directory.count() == 0
        assert Directory.get(directory.id) == null
        assert response.redirectedUrl == '/directory/list'
    }
}
