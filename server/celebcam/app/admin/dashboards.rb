ActiveAdmin::Dashboards.build do

  # Define your dashboard sections here. Each block will be
  # rendered on the dashboard in the context of the view. So just
  # return the content which you would like to display.
  
  section "Recently added cutouts" do
    table_for Cutout.all do |t|
				#t.column("Created") { |cutout| status_tag (cutout.created_at.strftime("%B %d, %Y")), (:ok) }
				t.column("Title") { |cutout| link_to cutout.title, admin_cutout_path(cutout) }
				t.column("Image") { |cutout| link_to cutout.image, admin_cutout_path(cutout) }
    end
  end
  
  section "Recently added photos" do
    table_for Photo.find(:all, :order => "created_at DESC") do |t|
      #t.column("Created") { |photo| status_tag (photo.created_at.strftime("%B %d, %Y")), (:ok) }
      t.column("Title") { |photo| link_to photo.cutout.title, admin_photo_path(photo) }
      t.column("Image") { |photo| link_to photo.image, admin_photo_path(photo) }
    end
  end
  
  section "Recently added products" do
    table_for Product.find(:all, :order => "created_at DESC") do |t|
      #t.column("Created") { |product| status_tag (product.created_at.strftime("%B %d, %Y")), (:ok) }
      t.column("Title") { |product| link_to product.title, admin_product_path(product) }
      t.column("Price") { |product| link_to "$%s" % product.price, admin_product_path(product) }
    end
  end
  
  
  # == Simple Dashboard Section
  # Here is an example of a simple dashboard section
  #
  #   section "Recent Posts" do
  #     ul do
  #       Post.recent(5).collect do |post|
  #         li link_to(post.title, admin_post_path(post))
  #       end
  #     end
  #   end
  
  # == Render Partial Section
  # The block is rendered within the context of the view, so you can
  # easily render a partial rather than build content in ruby.
  #
  #   section "Recent Posts" do
  #     div do
  #       render 'recent_posts' # => this will render /app/views/admin/dashboard/_recent_posts.html.erb
  #     end
  #   end
  
  # == Section Ordering
  # The dashboard sections are ordered by a given priority from top left to
  # bottom right. The default priority is 10. By giving a section numerically lower
  # priority it will be sorted higher. For example:
  #
  #   section "Recent Posts", :priority => 10
  #   section "Recent User", :priority => 1
  #
  # Will render the "Recent Users" then the "Recent Posts" sections on the dashboard.
  
  # == Conditionally Display
  # Provide a method name or Proc object to conditionally render a section at run time.
  #
  # section "Membership Summary", :if => :memberships_enabled?
  # section "Membership Summary", :if => Proc.new { current_admin_user.account.memberships.any? }

end
