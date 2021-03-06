/**
 * OTP 是 One-Time Password的简写，表示一次性密码,(一次一密)。
 * 名词解释
 * OTP 是 One-Time Password的简写，表示一次性密码。
 *
 * HOTP 是HMAC-based One-Time Password的简写，表示基于HMAC算法加密的一次性密码。
 *
 * TOTP 是Time-based One-Time Password的简写，表示基于时间戳算法的一次性密码。
 *
 *
 *
 * 基本介绍
 * 　　TOTP 是时间同步，基于客户端的动态口令和动态口令验证服务器的时间比对，一般每60秒产生一个新口令，要求客户端和服务器能够十分精确的保持正确的时钟，客户端和服务端基于时间计算的动态口令才能一致。
 * 　　HOTP 是事件同步，通过某一特定的事件次序及相同的种子值作为输入，通过HASH算法运算出一致的密码。
 * <p>
 * 计算OTP串的公式：
 * <pre>
 * OTP(K,C) = Truncate(HMAC-SHA-1(K,C))
 * K：表示秘钥串
 * C：是一个数字，表示随机数
 * Truncate：是一个函数，就是怎么截取加密后的串，并取加密后串的哪些字段组成一个数字。
 * </pre>
 * <pre>
 * 动态密码的解决方案有以下几个优点：
 * 解决用户在密码的记忆与保存上的困难性。
 * 由于密码只能使用一次，而且因为是动态产生，所以不可预测，也只有一次的使用有效性，可以大为提升使用的安全程度。
 * 基于这些优点，有越来越多的银行金融业甚至是游戏业使用OTP解决方案，来提升保护其用户的安全性。
 * </pre>
 * <pre>
 * 原理
 * 动态密码的产生方式，主要是以时间差作为服务器与密码产生器的同步条件。
 * 在需要登录的时候，就利用密码产生器产生动态密码，OTP一般分为计次使用以及计时使用两种，计次使用的OTP产出后，可在不限时间内使用；
 * 计时使用的OTP则可设置密码有效时间，从30秒到两分钟不等，而OTP在进行认证之后即废弃不用，下次认证必须使用新的密码，增加了试图不经授权访问有限制资源的难度。
 * </pre>
 *
 *
 *
 * @author Looly
 * @author wh
 */
package com.whaleal.icefrog.crypto.digest.otp;
