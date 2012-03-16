class Api::CutoutsController < ApplicationController 
  respond_to :xml, :json
   
# 
# Retrieve a list of cutouts
# 
# Example::		http:../cutouts
# Format::		JSON
# Method::		GET
#
# * *Returns* :
#   - list of cutout objects
#
  def index
    @cutouts = Cutout.all
		@res = Cutout.list(params, request)
		@cutouts = @res['items']
		
		if @res['cutout']
			@cutout = @res['cutout']
		end
		
    respond_to do |format|
      format.json { render :json => @res.to_json(:include => {:product => {} }) }
    end
  end

  # GET /cutouts/1
  # GET /cutouts/1.json
  def show # :nodoc:
    @cutout = Cutout.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render :json => @cutout }
    end
  end

  # GET /cutouts/new
  def new # :nodoc:
    @cutout = Cutout.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render :json => @cutout }
    end
  end

  # GET /cutouts/1/edit
  def edit # :nodoc:
    @cutout = Cutout.find(params[:id])
    
  end

  # POST /cutouts
  def create # :nodoc:
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
  def update # :nodoc:

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
  def destroy # :nodoc:
    @cutout = Cutout.find(params[:id])
    @cutout.destroy

    respond_to do |format|
      format.html { redirect_to cutouts_url }
      format.json { head :no_content }
    end
  end
end
