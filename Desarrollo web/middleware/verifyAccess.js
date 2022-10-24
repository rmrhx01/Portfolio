function verifyToken(req,res,next) {
    //console.log("I was here");
    
    const token = req.cookies.token || '';
    //console.log("Cookies: " + token)
    
    if (!token) {
        return res.redirect('/login');
    }
    else {
    next();
    }
    }
    
    
    module.exports = verifyToken;