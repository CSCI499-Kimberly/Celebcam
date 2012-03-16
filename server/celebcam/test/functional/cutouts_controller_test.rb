require 'test_helper'

class CutoutsControllerTest < ActionController::TestCase
  setup do
    @cutout = cutouts(:one)
  end

  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:cutouts)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create cutout" do
    assert_difference('Cutout.count') do
      post :create, :cutout => @cutout.attributes
    end

    assert_redirected_to cutout_path(assigns(:cutout))
  end

  test "should show cutout" do
    get :show, :id => @cutout
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => @cutout
    assert_response :success
  end

  test "should update cutout" do
    put :update, :id => @cutout, :cutout => @cutout.attributes
    assert_redirected_to cutout_path(assigns(:cutout))
  end

  test "should destroy cutout" do
    assert_difference('Cutout.count', -1) do
      delete :destroy, :id => @cutout
    end

    assert_redirected_to cutouts_path
  end
end
