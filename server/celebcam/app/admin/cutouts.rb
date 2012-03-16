ActiveAdmin.register Cutout do
	index :as => :grid do |cutout|
	  div :class => "cutout_image" do
	  	link_to image_tag(cutout.image), admin_cutout_path(cutout)
	  end
	end
	
  show do
    h3 cutout.title
    div do
      simple_format image_tag(cutout.image)
    end
  end
 
 
 form :html => { :enctype => "multipart/form-data" } do |f|
   f.inputs "Details" do
    f.input :title
    f.input :image, :as => :file
		f.input :product, :as => :select
  end
  f.buttons
 end  
end
