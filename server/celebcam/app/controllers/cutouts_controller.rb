class CutoutsController < ApplicationController
  # GET /cutouts
  # GET /cutouts.json
  def index
    @cutouts = Cutout.all
		@res = Cutout.list(params)
		@cutouts = @res['items']
		
		if @res['cutout']
			@cutout = @res['cutout']
		end
		
    respond_to do |format|
      format.html # index.html.erb
      format.json { render :json => @res }
    end
  end

  # GET /cutouts/1
  # GET /cutouts/1.json
  def show
    @cutout = Cutout.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @cutout }
    end
  end

  # GET /cutouts/new
  # GET /cutouts/new.json
  def new
    @cutout = Cutout.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @cutout }
    end
  end

  # GET /cutouts/1/edit
  def edit
    @cutout = Cutout.find(params[:id])
    
    
  end

  # POST /cutouts
  # POST /cutouts.json
  def create
    @res = Cutout.create(params, request)
		
    respond_to do |format|
      if not @res['error']
        format.html { redirect_to @res['item'], :notice => 'Cutout was successfully created.' }
        format.json { render :json => @res, :status => :created, :location => @res['item'] }
      else
        format.html { render :action => "new" }
        format.json { render :json => @res, :status => :unprocessable_entity }
      end
    end
  end

  # PUT /cutouts/1
  # PUT /cutouts/1.json
  def update

    @res = Cutout.update(params, request)    
    
    respond_to do |format|
      if not @res['error']
        format.html { redirect_to @res['item'], :notice => 'Cutout was successfully updated.' }
        format.json { render :json => @res }
      else
        format.html { render :action => "edit" }
        format.json { render :json => @res, :status => :unprocessable_entity }
      end
    end
  end

  # DELETE /cutouts/1
  # DELETE /cutouts/1.json
  def destroy
    @cutout = Cutout.find(params[:id])
    @cutout.destroy

    respond_to do |format|
      format.html { redirect_to cutouts_url }
      format.json { head :no_content }
    end
  end
end
