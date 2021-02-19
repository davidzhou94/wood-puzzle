package woodpuzzle.solver

import woodpuzzle.model.Configuration

/**
 * A configuration tree Node.
 * @author david
 */
internal class ConfigurationTreeNode (
        val parent: ConfigurationTreeNode?,
        val config: Configuration?
        )