class PhotosController < ApplicationController
  # GET /photos
  # GET /photos.json
  def index
    @photos = Photo.all
		@res = Photo.list(params)
		@photos = @res['items']
		
		if @res['photo']
			@photos = @res['photo']
		end
		
    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @res }
    end
  end

  # GET /photos/1
  # GET /photos/1.json
  def show
    @photo = Photo.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @photo }
    end
  end

  # GET /photos/new
  # GET /photos/new.json
  def new
    @photo = Photo.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @photo }
    end
  end

  # GET /photos/1/edit
  def edit
    @photo = Photo.find(params[:id])
  end

  # POST /photos
  # POST /photos.json
  def create
    @res = Photo.create(params, request)
		
    respond_to do |format|
      if not @res['error']
        format.html { redirect_to @res['item'], :notice => 'Photo was successfully created.' }
        format.json { render :json => @res, :status => :created, :location => @res['item'] }
      else
        format.html { render :action => "new" }
        format.json { render :json => @res, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /photos/1
  # PUT /photos/1.json
  def update
    @res = Photo.update(params, request)    
    
    respond_to do |format|
      if not @res['error']
        format.html { redirect_to @res['item'], :notice => 'Photo was successfully updated.' }
        format.json { render :json => @res }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @res, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /photos/1
  # DELETE /photos/1.json
  def destroy
    @photo = Photo.find(params[:id])
    @photo.destroy

    respond_to do |format|
      format.html { redirect_to photos_url }
      format.json { head :no_content }
    end
  end
end
