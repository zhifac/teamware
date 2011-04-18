DROP TABLE IF EXISTS `annotations`;
CREATE TABLE `annotations` (
  `docID` tinytext NOT NULL,
  `AnnotationID` tinytext NOT NULL,
  `startOffset` int(11) NOT NULL,
  `endOffset` int(11) NOT NULL,
  `AnnotationSet` text NOT NULL,
  `type` text NOT NULL,
  `name` text NOT NULL,
  `value` text NOT NULL,
  `indexStart` int(11) NOT NULL,
  `indexEnd` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Represents annotations on a document';

# --------------------------------------------------------

#
# Structure de la table `corpora`
#

DROP TABLE IF EXISTS `corpora`;
CREATE TABLE `corpora` (
  `corpusID` tinytext NOT NULL,
  `docID` tinytext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --------------------------------------------------------

#
# Structure de la table `docparams`
#

DROP TABLE IF EXISTS `docparams`;
CREATE TABLE `docparams` (
  `docID` tinytext NOT NULL,
  `name` text NOT NULL,
  `value` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --------------------------------------------------------

#
# Structure de la table `documents`
#

DROP TABLE IF EXISTS `documents`;
CREATE TABLE `documents` (
  `docID` tinytext NOT NULL,
  `text` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains the text for each document';

# --------------------------------------------------------

#
# Structure de la table `lock`
#

DROP TABLE IF EXISTS `lock`;
CREATE TABLE `lock` (
  `docID` tinytext NOT NULL,
  `AnnotationSet` text NOT NULL,
  `taskID` tinytext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='This table contains the locks for the AnnotationSets';
    