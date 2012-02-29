rails new celebcam
rails generate scaffold Cutout image_link:string
rails generate scaffold Photo image_link:string cutout_id:integer

rails generate scaffold Product title:string price:float


# cutout.rb
# class Cutout < ActiveRecord::Base
# 	has_many:photos
# end

# photo.rb
# class Photo < ActiveRecord::Base
# 	belongs_to :cutout
# end

# add root :to => 'cutouts#index' to routes.rb
rm public/index.html
rm -rf public/assets