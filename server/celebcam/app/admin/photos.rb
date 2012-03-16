ActiveAdmin.register Photo do
	index :as => :grid do |photo|
	  div :class => "photo_image" do
	  	link_to image_tag(photo.image), admin_photo_path(photo)
	  end
	end
	
  show do
    h3 photo.cutout.title
    div do
      simple_format image_tag(photo.image)
    end
  end
 
 
 form :html => { :enctype => "multipart/form-data" } do |f|
   f.inputs "Details" do
    f.input :image, :as => :file
		f.input :cutout, :as => :select
  end
  f.buttons
 end   
end
