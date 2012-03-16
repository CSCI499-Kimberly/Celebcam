require 'api'
class Photo < ActiveRecord::Base
	belongs_to :cutout
	
	has_attached_file :image, :styles => { :medium => "300x300>", :thumb => "100x100>" }
	
	def self.create(params, request)		
	  @photo = Photo.new()
	  @photo.save()
  	
  	if params[:photo][:image_link].class == ActionDispatch::Http::UploadedFile	
	  	uploaded_io = params[:photo][:image_link]
	  	logger.debug "Bieber: %s" % uploaded_io.original_filename
	  	
	  	filename = "%s%s" % [@photo.id, File.extname(uploaded_io.original_filename)]
	  	local_path = Rails.root.join('public', 'media/photos', filename)
	  	public_path = "http://%s%s%s" % [request.host_with_port, '/media/photos/', filename]
	  	File.open(local_path, 'wb') do |f|
	  		f.write(uploaded_io.read)
			end
	  	@photo.image_link = public_path
	  	@photo.save
	  end
		return Api.response({"item" => @photo, "kind" => "Photo"})
	end
	
	def self.list(params)
		@photos = Photo.all		
		return Api.response({"items" => @photos, "kind" => "Photos"})
	end
	
	
	def self.update(params, request)
		@photo = Photo.find(params[:id])
		@photo.update_attributes(params[:photo])
		
  	if params[:photo][:image_link].class == ActionDispatch::Http::UploadedFile	
	  	uploaded_io = params[:photo][:image_link]
	  	
	  	filename = "%s_%s%s" % [@photo.id, @photo.title, File.extname(uploaded_io.original_filename)]
	  	local_path = Rails.root.join('public', 'media', filename)
	  	public_path = "http://%s%s%s" % [request.host_with_port, '/media/', filename]
	  	File.open(local_path, 'wb') do |f|
	  		f.write(uploaded_io.read)
			end
	  	@photo.image_link = public_path
	  	@photo.save
	  end
	  
		return Api.response({"item" => @photo, "kind" => "Photo"})
	
	end

	
	
end
