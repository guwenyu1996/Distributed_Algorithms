public enum state {
    R,  //requesting the token
    E,  //executing the CS
    H,  //outside the CS, holding the token, and not aware of any requests
    O   //other
}
