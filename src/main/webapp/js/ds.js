var Stack = {
    createNew : function(){
        var stack = {};
        stack.elements = [];
        stack.size = function(){
            var len = stack.elements.length;
            return stack.elements.length;
        };
        stack.push = function(e){
            stack.elements.push(e);
        };
        stack.pop = function(){
            return stack.elements.pop();
        };
        stack.isEmpty = function(){
            return stack.size == 0;
        };
        stack.clear = function(){
            return stack.elements = [];
        };
        stack.top = function(){
            if (stack.isEmpty == true) return null;
            return stack.elements[stack.size() - 1];
        };
        return stack;
    }
}