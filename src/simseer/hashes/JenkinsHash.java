package simseer.hashes;

/*
 * Original copyright notice:
 * By Bob Jenkins, 1996.  bob_jenkins@burtleburtle.net.  You may use this code any way you wish, private, educational, or commercial.  Its free.
 * This implementation based on http://stackoverflow.com/questions/3279615/python-implementation-of-jenkins-hash2
*/

/*
 * This code works on the bytes that appear in the "data" string by first converting the String to a byte[] array.
 * The reason for doing this is to emulate the behavior of the original C implementation of the Jenkins hash.
 * By default using (int)charAt(i) returns the unicode value which would be different from the multiple byte values for non ASCII characters
 * To use unicode instead of bytes, operate directly on the string using charAt, rather than the byte array
 */

import java.math.BigInteger;

public class JenkinsHash {

    BigInteger deadbeef = BigInteger.valueOf(Long.decode("0xdeadbeef"));
    BigInteger xf = BigInteger.valueOf(Long.decode("0xffffffff"));

    public BigInteger rot(BigInteger x, int k) {
        return (x.shiftLeft(k)).or(x.shiftRight(32-k));
    }


    public BigInteger [] mix(BigInteger a, BigInteger b, BigInteger c) {

        a = a.and(xf);
        b = b.and(xf);
        c = c.and(xf);
        a = a.subtract(c);
        a = a.and(xf);
        a = a.xor(rot(c,4));
        a = a.and(xf);
        c = c.add(b);
        c = c.and(xf);
        b = b.subtract(a);
        b = b.and(xf);
        b = b.xor(rot(a,6));
        b = b.and(xf);
        a = a.add(c);
        a = a.and(xf);
        c = c.subtract(b);
        c = c.and(xf);
        c = c.xor(rot(b,8));
        c = c.and(xf);
        b = b.add(a);
        b = b.and(xf);
        a = a.subtract(c);
        a = a.and(xf);
        a = a.xor(rot(c,16));
        a = a.and(xf);
        c = c.add(b);
        c = c.and(xf);
        b = b.subtract(a);
        b = b.and(xf);
        b = b.xor(rot(a,19));
        b = b.and(xf);
        a = a.add(c);
        a = a.and(xf);
        c = c.subtract(b);
        c = c.and(xf);
        c = c.xor(rot(b,4));
        c = c.and(xf);
        b = b.add(a);
        b = b.and(xf);

        BigInteger [] abc = new BigInteger[3];
        abc[0] = a;
        abc[1] = b;
        abc[2] = c;

        return abc;

    }


    public BigInteger[] finals(BigInteger a, BigInteger b, BigInteger c) {

        a = a.and(xf);
        b = b.and(xf);
        c = c.and(xf);
        c = c.xor(b);
        c = c.and(xf);
        c = c.subtract(rot(b,14));
        c = c.and(xf);
        a = a.xor(c);
        a = a.and(xf);
        a = a.subtract(rot(c,11));
        a = a.and(xf);
        b = b.xor(a);
        b = b.and(xf);
        b = b.subtract(rot(a,25));
        b = b.and(xf);
        c = c.xor(b);
        c = c.and(xf);
        c = c.subtract(rot(b,16));
        c = c.and(xf);
        a = a.xor(c);
        a = a.and(xf);
        a = a.subtract(rot(c,4));
        a = a.and(xf);
        b = b.xor(a);
        b = b.and(xf);
        b = b.subtract(rot(a,14));
        b = b.and(xf);
        c = c.xor(b);
        c = c.and(xf);
        c = c.subtract(rot(b,24));
        c = c.and(xf);

        BigInteger [] abc = new BigInteger[3];
        abc[0] = a;
        abc[1] = b;
        abc[2] = c;

        return abc;
    }

    //This method returns the BigInteger value of one of the bytes in the data byte array, which may or may not be shifted
    //This method is just here to simplify the code
    private BigInteger bia(byte [] dataBytes, int p, int inc, int shift) {
        if (shift == 0) {
            return BigInteger.valueOf(dataBytes[p+inc] & 0xFF);
        }
        else {
            return BigInteger.valueOf( (dataBytes[p+inc] & 0xFF) << shift );
        }

    }

    public BigInteger [] hashlittle2(String data, int initval, int initval2) { //initval = 0, initval2 =0
    
        byte [] dataBytes = data.getBytes(); //Getting the byte array. Can operate directly on the string if desired

        int length = dataBytes.length;
        int lenpos = length;

        BigInteger a = deadbeef.add(BigInteger.valueOf(length)).add(BigInteger.valueOf(initval));
        BigInteger b = deadbeef.add(BigInteger.valueOf(length)).add(BigInteger.valueOf(initval));
        BigInteger c = deadbeef.add(BigInteger.valueOf(length)).add(BigInteger.valueOf(initval));

        BigInteger [] abc = new BigInteger[3];
        BigInteger [] cb = new BigInteger[2];

        c = c.add(BigInteger.valueOf(initval2));
        c = c.and(xf);

        int p = 0;
        
        while (lenpos > 12) {
            a = a.add(bia(dataBytes, p, 0, 0)).add(bia(dataBytes, p, 1, 8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
            a = a.and(xf);
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            b = b.and(xf);
            c = c.add(bia(dataBytes,p,8,0)).add(bia(dataBytes,p,9,8)).add(bia(dataBytes,p,10,16)).add(bia(dataBytes,p,11,24));
            c = c.and(xf);

            BigInteger [] abcMixed = mix(a,b,c);
            p += 12;
            lenpos -= 12;
            a = abcMixed[0];
            b = abcMixed[1];
            c = abcMixed[2];
        }

        if (lenpos == 12) {
            c = c.add(bia(dataBytes,p,8,0)).add(bia(dataBytes,p,9,8)).add(bia(dataBytes,p,10,16)).add(bia(dataBytes,p,11,24));
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 11) {
            c = c.add(bia(dataBytes,p,8,0)).add(bia(dataBytes,p,9,8)).add(bia(dataBytes,p,10,16));
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 10) {
            c = c.add(bia(dataBytes,p,8,0)).add(bia(dataBytes,p,9,8));
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 9) {
            c = c.add(bia(dataBytes,p,8,0));
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 8) {
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16)).add(bia(dataBytes,p,7,24));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 7) {
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8)).add(bia(dataBytes,p,6,16));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 6) {
            b = b.add(bia(dataBytes,p,4,0)).add(bia(dataBytes,p,5,8));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 5) {
            b = b.add(bia(dataBytes,p,4,0));
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 4) {
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16)).add(bia(dataBytes,p,3,24));
        }
        if (lenpos == 3) {
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8)).add(bia(dataBytes,p,2,16));
        }
        if (lenpos == 2) {
            a = a.add(bia(dataBytes,p,0,0)).add(bia(dataBytes,p,1,8));
        }
        if (lenpos == 1) {
            a = a.add(bia(dataBytes,p,0,0));
        }

        a = a.and(xf);
        b = b.and(xf);
        c = c.and(xf);

        if (lenpos == 0) {
            cb = new BigInteger [] {c, b};
            return cb;
        }

        abc = finals(a, b, c);
        cb = new BigInteger [] {abc[2], abc[1]};

        return cb;

    }

    public BigInteger hashlittle(String data, int initval) { //initval = 0
        BigInteger [] cb = hashlittle2(data, initval, 0);
        return cb[0];
    }


    public static void main (String [] args) {
        BigInteger [] hash2 = new JenkinsHash().hashlittle2(args[0], 0, 0);
        System.out.println(hash2[0] + " " + hash2[1]);
    }

}
