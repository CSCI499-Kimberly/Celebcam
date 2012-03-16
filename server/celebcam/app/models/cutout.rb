require 'api'
class Cutout < ActiveRecord::Base
	has_many:photos
	belongs_to :product
	
	#has_attached_file :image, :styles => { :medium => "300x300>", :thumb => "100x100>" }
	has_attached_file :image,
    :storage => :s3,
    :bucket => 'com.celebcam.celebcam',
    :s3_credentials => {
      :access_key_id => ENV['S3_KEY'],
      :secret_access_key => ENV['S3_SECRET']
    }, :styles => { :medium => "300x300>", :thumb => "100x100>" }
	
	
	
	def self.create(params, request)		
	  @cutout = Cutout.new(params[:cutout])
	  
	  @cutout.title = params[:cutout]['title']
	  @cutout.save()
	
#   	if params[:cutout][:image_link].class == ActionDispatch::Http::UploadedFile	
# 	  	uploaded_io = params[:cutout][:image_link]
# 	  	
# 	  	filename = "%s_%s%s" % [@cutout.id, @cutout.title, File.extname(uploaded_io.original_filename)]
# 	  	local_path = Rails.root.join('public', 'media/cutouts', filename)
# 	  	public_path = "http://%s%s%s" % [request.host_with_port, '/media/cutouts/', filename]
# 	  	File.open(local_path, 'wb') do |f|
# 	  		f.write(uploaded_io.read)
# 			end
# 	  	@cutout.image_link = public_path
# 	  	@cutout.save
# 	  end
		return Api.response({"item" => @cutout, "kind" => "cutout"})
	end
	
	def self.list(params, request)
		@cutouts = Cutout.all
		@cutouts.each do |cutout|
			cutout.image_link = cutout.image.url
		end
		
		return Api.response({"items" => @cutouts, "kind" => "cutouts"})
	end
	
	
	def self.update(params, request)
		
		@cutout = Cutout.find(params[:id])
		@cutout.update_attributes(params[:cutout])
		
#   	if params[:cutout][:image_link].class == ActionDispatch::Http::UploadedFile	
# 	  	uploaded_io = params[:cutout][:image_link]
# 	  	
# 	  	filename = "%s_%s%s" % [@cutout.id, @cutout.title, File.extname(uploaded_io.original_filename)]
# 	  	local_path = Rails.root.join('public', 'media', filename)
# 	  	public_path = "http://%s%s%s" % [request.host_with_port, '/media/', filename]
# 	  	File.open(local_path, 'wb') do |f|
# 	  		f.write(uploaded_io.read)
# 			end
# 	  	@cutout.image_link = public_path
# 	  	@cutout.save
# 	  end
	  
		return Api.response({"item" => @cutout, "kind" => "cutout"})
	
	end
	
end
