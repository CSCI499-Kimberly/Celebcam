class Api
	def self.response(params)    
    @res = params
    if @res['items']
    	@res['total_items'] = @res['items'].count
    end
    return @res
  end
end