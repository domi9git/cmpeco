# cmpeco

**cmpeco**, also known as _CMP Economy_ is a simple 1.17 balance and payment
plugin, that allows you to exchange your money between your wallet and bank
account, as well as give people money from your wallet (but only if they are
close enough!) or via bank transfer (must be transferred by 3rd party)

## Installation

### From source

1. Make sure you have:

   - OpenJDK 16 ([download](https://adoptopenjdk.net/))
   - Apache Maven ([download](https://maven.apache.org/))

2. Clone the repositiory:

   ```sh
   git clone https://github.com/qeamlgit/cmpeco && cd cmpeco
   ```

3. Build:

   ```sh
   mvn install -f pom.xml
   ```

4. Copy the JAR file from the 'target' folder to your server's plugin folder.
   The plugin will automatically create a config file for you to modify to your
   liking.

## Commands

**Not all of these commands are implemented!**

- `/balance` or `/bal` shows your current wallet & bank accoun balances
- `/pay <player> <amount>` transfers `amount` of currency to `player`, only if
  `player` is within a certain amount of blocks of the user (configurable)
- `/deposit <amount>` deposit `amount` of currency to your bank account from
  your wallet
- `/withdraw <amount>` withdraw `amount` of currency from your bank account to
  your wallet
- `/transfer <from> <to> <amount>` transfers `amount` of currency from `from`'s
  bank account to `to`'s bank account. **Requires `cmp.eco.bank` permission.**

## Permissions

- `cmp.eco.bank` allows you to make and manage bank transfers.

## Configuration settings

- `currency` is the symbol used for the currency in-game. Default is `"€"`.
- `starter-money` is the amount of currency every player starts with. Default is
  `500.0`.
- `pay-distance` is the farthest distance (in blocks) from which the `/pay`
  command will work. Default is `2.147`.
- `pay-threshold` is the highest amount of money that can be used with `/pay`.
  Default is `250.0`.
